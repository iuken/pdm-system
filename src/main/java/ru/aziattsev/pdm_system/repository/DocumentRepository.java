package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.aziattsev.pdm_system.dto.DocumentDto;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findFirstByFilePath(String filePath);

    List<Document> findByProject(CadProject cadProject);

    List<Document> findByProjectAndIsExistTrue(CadProject project);

    @Query("""
                select new ru.aziattsev.pdm_system.dto.DocumentDto(
                    d.id,
                    d.filePath,
                    d.status,
                    lm.displayName,
                    r.displayName
                )
                from Document d
                left join d.lastModify lm
                left join d.responsible r
                where d.project.id = :projectId
                  and d.isExist = true
            """)
    List<DocumentDto> findAllByProjectIdWithDto(@Param("projectId") Long projectId);

}
