package ru.aziattsev.pdm_system.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.Document;
import ru.aziattsev.pdm_system.entity.DocumentRequest;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.repository.DocumentRepository;
import ru.aziattsev.pdm_system.repository.ItemRepository;

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
    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ItemRepository itemRepository;

    void UploadFromPath(String projectPath, String headAssemblyPath) {
        File dir = new File(projectPath);
        try (Stream<Path> stream = Files.walk(dir.toPath())) {
            stream.filter(file -> FilenameUtils.getExtension(file.toString()).equals("grb")).map(file -> {
                try {
                    return new Document(file.toString(), new Date(Files.readAttributes(file, BasicFileAttributes.class).creationTime().toMillis()), new Date(Files.readAttributes(file, BasicFileAttributes.class).lastModifiedTime().toMillis()));
                } catch (IOException e) {
                    return new Document(file.toString());
                }
            }).forEach(this::update);
        } catch (IOException e) {
        }
    }


    @Transactional
    public void update(Document document) {
        Optional<Document> document1 = documentRepository.findFirstByFilePath(document.getFilePath());
        if (document1.isPresent()) {
            document.setId(document1.get().getId());
        }
        documentRepository.save(document);

        Optional<Item> itemOptional = itemRepository.findFirstByDocument(document);
        if (itemOptional.isEmpty()) {
            itemRepository.save(new Item(document));
        }

    }

    public void init() {
        UploadFromPath("C:\\Users\\Александр Азиатцев\\Desktop\\НТКМ", "");
    }

    @Transactional
    public void updateFromCad(DocumentRequest documentRequest) {
        Optional<Document> document1 = documentRepository.findFirstByFilePath(documentRequest.filePath());
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

        //поиск объекта связаного с этим документом
        Optional<Item> itemOptional = itemRepository.findFirstByDocument(document);
        //если такой обект существует ничего не делаем, ссылка обновится
        //если не существует
        if (itemOptional.isEmpty()) {
            //создаем объект
            Item item = new Item(document);
            //Сохраняем в бд
            itemRepository.save(item);
        }
    }
}
