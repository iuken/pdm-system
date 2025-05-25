package ru.aziattsev.pdm_system.services;

import org.springframework.stereotype.Service;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CadProjectService {
    private final CadProjectRepository repository;

    public CadProjectService(CadProjectRepository repository) {
        this.repository = repository;
    }

    public List<CadProject> findAll() {
        return repository.findAll().stream()
                .peek(project -> {
                    project.setDirectory(PathConverter.toClientPath(project.getDirectory()));
                    project.setMainAssemblyPath(PathConverter.toClientPath(project.getMainAssemblyPath()));
                    project.setStructurePath(PathConverter.toClientPath(project.getStructurePath()));
                })
                .collect(Collectors.toList());
    }

    public CadProject save(CadProject project) {
        project.setDirectory(PathConverter.toServerPath(project.getDirectory()));
        project.setMainAssemblyPath(PathConverter.toServerPath(project.getMainAssemblyPath()));
        project.setStructurePath(PathConverter.toServerPath(project.getStructurePath()));
        return repository.save(project);
    }

    public Optional<CadProject> findById(Long id) {
//        return repository.findById(id).map(project -> {
//            project.setDirectory(PathConverter.toClientPath(project.getDirectory()));
//            project.setMainAssemblyPath(PathConverter.toClientPath(project.getMainAssemblyPath()));
//            project.setStructurePath(PathConverter.toClientPath(project.getStructurePath()));
//            return project;
//        });
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}