package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aziattsev.pdm_system.entity.XmlTree;

import java.util.List;
import java.util.Optional;

public interface XmlTreeRepository extends JpaRepository<XmlTree, Long> {
    Optional<XmlTree> findBySourceNameAndRootObjectId(String sourceName, String rootObjectId);

    List<XmlTree> findByProjectId(Long projectId);

}