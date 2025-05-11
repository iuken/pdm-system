package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.entity.XmlTree;

import java.util.List;
import java.util.Optional;


public interface EngineeringElementRepository extends JpaRepository<EngineeringElement, Long> {
    List<EngineeringElement> findByTreeId(Long treeId);

    List<EngineeringElement> findByParentId(Long parentId);

    Optional<EngineeringElement> findByTreeAndObjectId(XmlTree tree, String objectId);

    void deleteByTree(XmlTree tree);

    List<EngineeringElement> findByNameAndDesignation(String name, String designation);

    @Query("SELECT SUM(e.quantity) FROM EngineeringElement e WHERE e.item = :item")
    Integer sumQuantityByItem(@Param("item") Item item);

}