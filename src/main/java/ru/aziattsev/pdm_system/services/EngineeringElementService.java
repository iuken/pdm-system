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
        return elementRepository.sumQuantityByDesignationInSection(section, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySection(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByDesignationInSection(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllExcludeSection(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByDesignationNotInSection(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySectionWithParent(String section, Long cadProject) {
        return elementRepository.sumQuantityByDesignationInSectionWithParent(section, cadProject);
    }

    public List<EngineeringElementDto>  findAllBySectionWithParent(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByDesignationInSectionsWithParent(sectionList, cadProject);
    }

    public List<EngineeringElementDto>  findAllExcludeSectionWithParent(List<String> sectionList, Long cadProject) {
        return elementRepository.sumQuantityByDesignationNotInSectionsWithParent(sectionList, cadProject);
    }
}
