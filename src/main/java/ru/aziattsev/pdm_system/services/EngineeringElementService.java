package ru.aziattsev.pdm_system.services;

import org.springframework.stereotype.Service;
import ru.aziattsev.pdm_system.dto.EngineeringElementDto;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EngineeringElementService {

    private final EngineeringElementRepository elementRepository;

    public EngineeringElementService(EngineeringElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    public List<EngineeringElement> findAll() {
        return elementRepository.findAll();
    }

    public List<EngineeringElementDto>  findAllBySection(String section, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationInSection(section, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySection(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationInSection(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllExcludeSection(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationNotInSection(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySectionWithParent(String section, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationInSectionWithParent(section, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySectionWithParent(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationInSectionsWithParent(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllExcludeSectionWithParent(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByFullDesignationNotInSectionsWithParent(sectionList, cadProject);
    }
    public List<EngineeringElementDto>  findBySameDesignationAndDifferentNames(Long cadProject) {
        return elementRepository.findBySameDesignationAndDifferentNames(cadProject);
    }
    public List<EngineeringElementDto>  findUnusedAssemblyUnits(Long cadProject) {
        return elementRepository.findUnusedAssemblyUnits(cadProject);
    }
}
