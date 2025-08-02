package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.aziattsev.pdm_system.dto.ElementDocumentPair;
import ru.aziattsev.pdm_system.dto.EngineeringElementDto;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.entity.XmlTree;

import java.util.List;
import java.util.Optional;

public interface EngineeringElementRepository extends JpaRepository<EngineeringElement, Long> {
    List<EngineeringElement> findByTreeId(Long treeId);
    List<EngineeringElement> findAllByCadProject_Id(Long projectId);

    List<EngineeringElement> findByParentId(Long parentId);

    Optional<EngineeringElement> findByTreeAndObjectId(XmlTree tree, String objectId);

    void deleteByTree(XmlTree tree);

    List<EngineeringElement> findByNameAndDesignation(String name, String designation);

    void deleteByTreeId(Long treeId);
    @Query("""
    SELECT new ru.aziattsev.pdm_system.dto.ElementDocumentPair(e, d)
    FROM EngineeringElement e
    JOIN Document d
      ON e.designation = d.designation AND e.name = d.name
    WHERE e.cadProject.id = :projectId AND d.project.id = :projectId
""")
    List<ElementDocumentPair> findElementsWithDocumentsByProjectId(@Param("projectId") Long projectId);




    @Query("SELECT SUM(e.quantity) FROM EngineeringElement e WHERE e.item = :item")
    Double sumQuantityByItem(@Param("item") Item item);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.fullDesignation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section = :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name, e.section")
    List<EngineeringElementDto> sumQuantityByFullDesignationInSection(@Param("section") String section,
                                                       @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.fullDesignation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section IN :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name ,e.section")
    List<EngineeringElementDto> sumQuantityByFullDesignationInSection(@Param("section") List<String> section,
                                                         @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(e.fullDesignation, e.name, e.section, SUM(e.quantity), '') " +
            "FROM EngineeringElement e " +
            "WHERE e.section NOT  IN :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name, e.section")
    List<EngineeringElementDto> sumQuantityByFullDesignationNotInSection(@Param("section") List<String> section,
                                                         @Param("cadProjectId") Long cadProjectId);


    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section = :section AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> sumQuantityByFullDesignationInSectionWithParent(
            @Param("section") String section,
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section IN :sections AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> sumQuantityByFullDesignationInSectionsWithParent(
            @Param("sections") List<String> sections,
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section NOT IN :sections AND e.cadProject.id = :cadProjectId " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> sumQuantityByFullDesignationNotInSectionsWithParent(
            @Param("sections") List<String> sections,
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.cadProject.id = :cadProjectId " +
            "AND e.fullDesignation <> '' " +
            "AND e.fullDesignation IN (" +
            "   SELECT e2.fullDesignation " +
            "   FROM EngineeringElement e2 " +
            "   WHERE e2.fullDesignation <> '' " +
            "   GROUP BY e2.fullDesignation " +
            "   HAVING COUNT(DISTINCT e2.name) > 1" +
            ") " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> findBySameDesignationAndDifferentNames(@Param("cadProjectId") Long cadProject);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.section = 'Сборочные единицы' " +
            "AND e.cadProject.id = :cadProjectId " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM EngineeringElement child " +
            "    WHERE child.parent.fullDesignation = e.fullDesignation" +
            ") " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> findUnusedAssemblyUnits(
            @Param("cadProjectId") Long cadProjectId);

    @Query("SELECT new ru.aziattsev.pdm_system.dto.EngineeringElementDto(" +
            "e.fullDesignation, e.name, e.section, SUM(e.quantity), " +
            "COALESCE(parent.fullDesignation, '')) " +
            "FROM EngineeringElement e " +
            "LEFT JOIN e.parent parent " +
            "WHERE e.name LIKE %:pattern% " +
            "GROUP BY e.fullDesignation, e.name, e.section, parent.fullDesignation")
    List<EngineeringElementDto> findByName(@Param("pattern") String pattern);
}