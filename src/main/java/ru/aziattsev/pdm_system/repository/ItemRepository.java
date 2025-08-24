package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.aziattsev.pdm_system.dto.ItemDto;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Document;
import ru.aziattsev.pdm_system.entity.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findFirstByDocument(Document document);

    List<Item> findAllByProject(CadProject cadProject);

    Optional<Item> findByDocument(Document document);

    List<Item> findByDocumentIdIn(Set<Long> documentIds);

    List<Item> findByProjectAndDocumentIsExistTrue(CadProject cadProject);

    @Query("select new ru.aziattsev.pdm_system.dto.ItemDto(" +
            "i.id, " +
            "d.filePath, " +
            "i.status, " +
            "lm.displayName, " +
            "r.displayName) " +
            "from Item i " +
            "left join i.document d " +
            "left join i.lastModify lm " +
            "left join i.responsible r " +
            "where i.project.id = :projectId " +
            "and d.isExist = true")
    List<ItemDto> findAllByProjectIdWithDto(@Param("projectId") Long projectId);
}
