package ru.aziattsev.pdm_system.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.*;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;
import ru.aziattsev.pdm_system.repository.DocumentRepository;
import ru.aziattsev.pdm_system.repository.ItemRepository;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;


    private final ItemRepository itemRepository;
    private final CadProjectRepository cadProjectRepository;
    private final PdmUserRepository pdmUserRepository;

    public DocumentService(DocumentRepository documentRepository, ItemRepository itemRepository, CadProjectRepository cadProjectRepository, PdmUserRepository pdmUserRepository) {
        this.documentRepository = documentRepository;
        this.itemRepository = itemRepository;
        this.cadProjectRepository = cadProjectRepository;
        this.pdmUserRepository = pdmUserRepository;
    }

    public void UploadFromPath(String projectPath, Long projectId) {
        CadProject cadProject = cadProjectRepository.getReferenceById(projectId);
        File dir = new File(projectPath);
        try (Stream<Path> stream = Files.walk(dir.toPath())) {
            stream.filter(file -> FilenameUtils.getExtension(file.toString()).equals("grb"))
                    .map(file -> {
                        try {
                            String normalizedPath = file.toAbsolutePath().normalize().toString();
                            String serverPath = PathConverter.toServerPath(normalizedPath);
                            System.out.println(normalizedPath);
                            Date creationTime = new Date(Files.readAttributes(file, BasicFileAttributes.class).creationTime().toMillis());
                            Date lastModifiedTime = new Date(Files.readAttributes(file, BasicFileAttributes.class).lastModifiedTime().toMillis());
                            return new Document(serverPath, creationTime, lastModifiedTime);
                        } catch (IOException e) {
                            String normalizedPath = file.toAbsolutePath().normalize().toString();
                            String serverPath = PathConverter.toServerPath(normalizedPath);
                            return new Document(serverPath);
                        }
                    })
                    .peek(document -> document.setProject(cadProject))
                    .forEach(this::update);
        } catch (IOException e) {
            e.printStackTrace(); // или логгер
        }
    }


    @Transactional
    public void update(Document document) {
        System.out.println("внутри метода update");
        Optional<Document> document1 = documentRepository.findFirstByFilePath(document.getFilePath());
        if (document1.isPresent()) {
            System.out.println("документ найден");
            document.setId(document1.get().getId());
        }
        if (!document1.isPresent()) {
            System.out.println("документ не найден");
        }
        documentRepository.save(document);

        Optional<Item> itemOptional = itemRepository.findFirstByDocument(document);
        if (itemOptional.isEmpty()) {
            itemRepository.save(new Item(document));
        }

    }

    @Transactional
    public void updateFromCad(DocumentRequest documentRequest) {
        Optional<Document> document1 = documentRepository.findFirstByFilePath(PathConverter.toServerPath(documentRequest.filePath()));
        DocumentStatus documentStatus = DocumentStatus.UNDEFINED;
        Document document;
        if (document1.isPresent()) {
            document = document1.get();

        } else {
            document = new Document();
        }
        document.setDesignation(documentRequest.designation());
        document.setName(documentRequest.name());
        document.setModelMaker(documentRequest.modelMaker());
        document.setDrawing(documentRequest.drawing());
        document.setChecking(documentRequest.checking());
        document.setStandardControl(documentRequest.standardControl());
        document.setTechnicalControl(documentRequest.technicalControl());
        document.setApproved(documentRequest.approved());
        document.setMaterial2(documentRequest.material2());
        document.setMaterial3(documentRequest.material3());
        document.setMaterial4(documentRequest.material4());
        document.setMass(documentRequest.mass());
        document.setxSize(documentRequest.xSize());
        document.setySize(documentRequest.ySize());
        document.setzSize(documentRequest.zSize());
        documentRepository.save(document);

        documentStatus = documentRequest.documentStatus();
        Item item;
        //поиск объекта связаного с этим документом
        Optional<Item> itemOptional = itemRepository.findFirstByDocument(document);
        //если такой обект существует ничего не делаем, ссылка обновится
        //если не существует
        if (itemOptional.isEmpty()) {
            //создаем объект
            item = new Item(document);
            item.setStatus(DocumentStatus.UNDEFINED);
            //Сохраняем в бд
        }
        else {
            item = itemOptional.get();
        }

        //запоминаем предыдущий статус
        DocumentStatus previousStatus = item.getStatus();

        //устанавливаем новый статус
        item.setStatus(documentStatus);

        //устанавливаем последнего изменившего
        Optional<PdmUser> lastModifyOptional = pdmUserRepository.findByUsername(documentRequest.user());
        PdmUser lastModify = lastModifyOptional.get();
        item.setLastModify(lastModify);

        //устанавливаем разработчика
        if (documentStatus == DocumentStatus.MARKED_AS_READY){
            item.setDeveloper(lastModify);
        }


        item.setResponsible(getResponsible(item, documentStatus, previousStatus));

        itemRepository.save(item);
    }

    /*
    назначение ответственного по статусам и послденему изменившему
     порядок согласования:
подпись разработчика,
 проверяющего,
 техконтроля,
 нормоконтроля,
 удверждение

 если подписал разработчик, нужно смотреть кто последний подписывал и ему вернуть документ
 разработчик смоделировал, начертил подписывает MARKED_AS_NOT_READY, предыдушего статуса не было
 документ переходит проверяющему и подписывает CHECKER_MARKED_AS_NOT_READY, смотрим кто поставил статус готовности и
 возвращаем ему с новым статусом
 разработчик вновь корректирует ставит статус MARKED_AS_NOT_READY предыдущий статус был от проверяющего, ему и возвращаем
 проверяющий подписывает со статусом CHECKER_MARKED_AS_READY документ переходит технологу
 техконтроль проверяет подписывает TECHNICAL_CONTROL_MARKED_AS_NOT_READY и документ должен вернуться РАЗРАБОТЧИКУ(ЕГО НУЖНО ПОМНИТЬ)
 разработчик подписывает MARKED_AS_NOT_READY и документ возвращает согласно предыдущему статусу - технологу

 если техконтроля в проекте нет, документ должен сразу уйти нормоконтролю аналогично

 item должен хранить пользователя разработчика, чтоб вернуть ему на любом этапе подписания
 проект может иметь множетсво разработчиков и единственных проверящих, нормоконтролера и утверждающего, их не храним в item

     */

    private PdmUser getResponsible(Item item, DocumentStatus documentStatus, DocumentStatus previousStatus){
        CadProject cadProject = item.getProject();
        switch (documentStatus) {
            case MARKED_AS_READY: {
                if  (previousStatus != null){
                    switch (previousStatus) {
                        case CHECKER_MARKED_AS_NOT_READY: {
                            return cadProject.getChecking();
                        }
                        case TECHNICAL_CONTROL_MARKED_AS_NOT_READY: {
                            return cadProject.getTechnicalControl();
                        }
                        case STANDARD_CONTROL_MARKED_AS_NOT_READY: {
                            return cadProject.getStandardControl();
                        }
                        case APPROVED_MARKED_AS_NOT_READY: {
                            return cadProject.getApproved();
                        }
                    }
                }
                else return cadProject.getChecking();

            }
            case CHECKER_MARKED_AS_NOT_READY:{
                return item.getDeveloper();
            }
            case CHECKER_MARKED_AS_READY:{
                return cadProject.getTechnicalControl()==null?cadProject.getStandardControl():cadProject.getTechnicalControl();
            }
            case STANDARD_CONTROL_MARKED_AS_NOT_READY:{
                return item.getDeveloper();
            }
            case STANDARD_CONTROL_MARKED_AS_READY:{
                return cadProject.getApproved();
            }
            case TECHNICAL_CONTROL_MARKED_AS_NOT_READY:{
                return item.getDeveloper();
            }
            case TECHNICAL_CONTROL_MARKED_AS_READY:{
                return cadProject.getStandardControl();
            }
            case APPROVED_MARKED_AS_NOT_READY:{
                return item.getDeveloper();
            }
            case APPROVED_MARKED_AS_READY:{
                return null;
            }
        }
        return null;
    }
}
