package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.entity.XmlTree;

import java.util.List;
import java.util.Optional;


public interface EngineeringElementRepository extends JpaRepository<EngineeringElement, Long> {
    List<EngineeringElement> findByTreeId(Long treeId);
    List<EngineeringElement> findByParentId(Long parentId);

    Optional<EngineeringElement> findByTreeAndObjectId(XmlTree tree, String objectId);
    void deleteByTree(XmlTree tree);
}