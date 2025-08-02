package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.aziattsev.pdm_system.entity.Document;

import java.util.Date;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findFirstByFilePath(String filePath);
    Optional<Document> findByProjectIdAndDesignationAndName(Long projectId, String designation, String name);
    @Modifying
    @Query("update Document set lastModifiedTime = :lastModifiedTime, creationTime = :creationTime where id = :documentId")
    void setDocumentInfoById(Date lastModifiedTime, Date creationTime, Long documentId);
    Optional<Document> findByDesignationAndName(String designation, String name);
//    void
//
//    @Transactional
//    default Document saveOrUpdateIfExist(Document entity) {
//        Optional<Long> docId = getIdByFilePath(entity.getFilePath());
//        docId.ifPresent();
//        return save(entity);
//    }


}
