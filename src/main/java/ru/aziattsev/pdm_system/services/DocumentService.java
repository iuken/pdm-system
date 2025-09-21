package ru.aziattsev.pdm_system.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.dto.DocumentDto;
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
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
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

    @Transactional
    public void UploadFromPath(String projectPath, Long projectId) {
        CadProject cadProject = cadProjectRepository.getReferenceById(projectId);

        // Сбрасываем isExist для всех документов проекта
        List<Document> existingDocuments = documentRepository.findByProject(cadProject);
        existingDocuments.forEach(doc -> doc.setExist(false));
        documentRepository.saveAll(existingDocuments);

        File dir = new File(projectPath);

        try (Stream<Path> stream = Files.walk(dir.toPath())) {
            stream.filter(file -> "grb".equalsIgnoreCase(FilenameUtils.getExtension(file.toString())))
                    .map(file -> createDocumentFromFile(file, cadProject))
                    .forEach(this::updateDocument); // обновляем документ в БД
        } catch (IOException e) {
            e.printStackTrace(); // или использовать логгер
        }
    }

    private Document createDocumentFromFile(Path file, CadProject project) {
        try {
            String normalizedPath = file.toAbsolutePath().normalize().toString();
            String serverPath = PathConverter.toServerPath(normalizedPath);
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            Date creationTime = new Date(attrs.creationTime().toMillis());
            Date lastModifiedTime = new Date(attrs.lastModifiedTime().toMillis());

            Document doc = new Document(serverPath, creationTime, lastModifiedTime, true);
            doc.setProject(project);
            return doc;
        } catch (IOException e) {
            String normalizedPath = file.toAbsolutePath().normalize().toString();
            String serverPath = PathConverter.toServerPath(normalizedPath);

            Document doc = new Document(serverPath, true);
            doc.setProject(project);
            return doc;
        }
    }

    @Transactional
    public void updateDocument(Document document) {
        Optional<Document> existingDoc = documentRepository.findFirstByFilePath(document.getFilePath());

        existingDoc.ifPresent(doc -> document.setId(doc.getId())); // если найден — обновляем существующий

        documentRepository.save(document); // сохраняем или обновляем
    }

    @Transactional
    public void updateFromCad(DocumentRequest documentRequest) {
        Optional<Document> documentOptional = documentRepository.findFirstByFilePath(
                PathConverter.toServerPath(documentRequest.filePath())
        );

        Document document = documentOptional.orElseGet(Document::new);

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

        // статус документа
        DocumentStatus previousStatus = document.getStatus();
        DocumentStatus newStatus = documentRequest.documentStatus();
        document.setStatus(newStatus);

        // устанавливаем последнего изменившего
        PdmUser lastModify = pdmUserRepository
                .findByUsername(documentRequest.user())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + documentRequest.user()));
        document.setLastModify(lastModify);

        // устанавливаем разработчика
        if (newStatus == DocumentStatus.MARKED_AS_READY) {
            document.setDeveloper(lastModify);
        }

        // ответственный определяется бизнес-логикой
        document.setResponsible(getResponsible(document, newStatus, previousStatus));

        documentRepository.save(document);
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

    private PdmUser getResponsible(Document document, DocumentStatus documentStatus, DocumentStatus previousStatus) {
        CadProject cadProject = document.getProject();
        switch (documentStatus) {
            case MARKED_AS_READY: {
                if (previousStatus != null) {
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
                } else return cadProject.getChecking();

            }
            case CHECKER_MARKED_AS_NOT_READY: {
                return document.getDeveloper();
            }
            case CHECKER_MARKED_AS_READY: {
                return cadProject.getTechnicalControl() == null ? cadProject.getStandardControl() : cadProject.getTechnicalControl();
            }
            case STANDARD_CONTROL_MARKED_AS_NOT_READY: {
                return document.getDeveloper();
            }
            case STANDARD_CONTROL_MARKED_AS_READY: {
                return cadProject.getApproved();
            }
            case TECHNICAL_CONTROL_MARKED_AS_NOT_READY: {
                return document.getDeveloper();
            }
            case TECHNICAL_CONTROL_MARKED_AS_READY: {
                return cadProject.getStandardControl();
            }
            case APPROVED_MARKED_AS_NOT_READY: {
                return document.getDeveloper();
            }
            case APPROVED_MARKED_AS_READY: {
                return null;
            }
        }
        return null;
    }

    public List<Document> findAllByProjectIdWithExistedDocument(Long id) {
        CadProject cadProject = cadProjectRepository.getReferenceById(id);

        // Берём только документы, у которых isExist = true
        List<Document> documents = documentRepository.findByProjectAndIsExistTrue(cadProject);

        // компилируем regex-паттерны, но игнорируем битые
        List<Pattern> ignorePatterns = cadProject.getIgnorePatterns().stream()
                .map(p -> {
                    try {
                        return Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                    } catch (PatternSyntaxException e) {
                        // log.warn("Некорректный regex в проекте {}: '{}'", cadProject.getId(), p);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return documents.stream()
                .filter(doc -> {
                    if (doc.getFilePath() == null) return false;

                    String filePath = doc.getFilePath().replace("\\", "/");

                    // исключаем документ, если он совпадает хотя бы с одним regex
                    return ignorePatterns.stream().noneMatch(p -> p.matcher(filePath).matches());
                })
                .toList();
    }

    public List<DocumentDto> findFilteredByProjectId(Long projectId,
                                                     String filename,
                                                     String status,
                                                     String lastModify,
                                                     String responsible) {
        List<DocumentDto> documents = findAllByProjectIdWithDto(projectId);

        return documents.stream()
                .filter(dto -> filename == null || filename.isBlank() ||
                        (dto.getClientFilePath() != null &&
                                dto.getClientFilePath().toLowerCase().contains(filename.toLowerCase())))
                .filter(dto -> status == null || status.isBlank() ||
                        (dto.getStatusDisplayName() != null &&
                                dto.getStatusDisplayName().toLowerCase().contains(status.toLowerCase())))
                .filter(dto -> lastModify == null || lastModify.isBlank() ||
                        (dto.getLastModifyDisplayName() != null &&
                                dto.getLastModifyDisplayName().toLowerCase().contains(lastModify.toLowerCase())))
                .filter(dto -> responsible == null || responsible.isBlank() ||
                        (dto.getResponsibleDisplayName() != null &&
                                dto.getResponsibleDisplayName().toLowerCase().contains(responsible.toLowerCase())))
                .sorted(Comparator.comparing(DocumentDto::getClientFilePath, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<DocumentDto> findAllByProjectIdWithDto(Long projectId) {
        CadProject cadProject = cadProjectRepository.getReferenceById(projectId);

        // компилируем regex-паттерны игнорирования
        List<Pattern> ignorePatterns = cadProject.getIgnorePatterns().stream()
                .map(p -> {
                    try {
                        return Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                    } catch (PatternSyntaxException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return documentRepository.findAllByProjectIdWithDto(projectId).stream()
                // фильтр по существующим документам и игнорируемым паттернам
                .filter(dto -> {
                    if (dto.getClientFilePath() == null) return false;
                    String filePath = dto.getClientFilePath().replace("\\", "/");
                    return ignorePatterns.stream().noneMatch(p -> p.matcher(filePath).matches());
                })
                .map(dto -> {
                    String clientPath = PathConverter.toClientPath(dto.getClientFilePath());
                    return new DocumentDto(
                            dto.getId(),
                            clientPath,
                            dto.getStatus(),
                            dto.getLastModifyDisplayName() != null ? dto.getLastModifyDisplayName() : "Не указан",
                            dto.getResponsibleDisplayName() != null ? dto.getResponsibleDisplayName() : "Не указан"
                    );
                })
                .toList();
    }

}
