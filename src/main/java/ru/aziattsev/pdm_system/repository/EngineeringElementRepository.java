package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.aziattsev.pdm_system.dto.EngineeringElementDto;
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

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.designation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section = :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name, e.section")
    List<EngineeringElementDto> sumQuantityByDesignationInSection(@Param("section") String section,
                                                       @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.designation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section IN :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name ,e.section")
    List<EngineeringElementDto> sumQuantityByDesignationInSection(@Param("section") List<String> section,
                                                         @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.designation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section NOT  IN :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name, e.section")
    List<EngineeringElementDto> sumQuantityByDesignationNotInSection(@Param("section") List<String> section,
                                                         @Param("cadProjectId") Long cadProjectId);


    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.designation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.designation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section = :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name, e.section, parent.designation")
    List<EngineeringElementDto> sumQuantityByDesignationInSectionWithParent(
            @Param("section") String section,
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.designation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.designation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section IN :sections AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name, e.section, parent.designation")
    List<EngineeringElementDto> sumQuantityByDesignationInSectionsWithParent(
            @Param("sections") List<String> sections,
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.designation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.designation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section NOT IN :sections AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.designation, e.name, e.section, parent.designation")
    List<EngineeringElementDto> sumQuantityByDesignationNotInSectionsWithParent(
            @Param("sections") List<String> sections,
            @Param("cadProjectId") Long cadProjectId);
}