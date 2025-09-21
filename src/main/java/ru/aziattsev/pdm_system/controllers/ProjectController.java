package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.dto.DocumentDto;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;
import ru.aziattsev.pdm_system.services.CadProjectService;
import ru.aziattsev.pdm_system.services.DocumentService;
import ru.aziattsev.pdm_system.services.ItemService;

import java.util.*;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final CadProjectService projectService;
    private final ItemService itemService;
    private final PdmUserRepository userRepository;

    private final DocumentService documentService;
    public ProjectController(CadProjectService projectService, ItemService itemService, PdmUserRepository userRepository, DocumentService documentService) {
        this.projectService = projectService;
        this.itemService = itemService;
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @GetMapping
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "projects/list";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
        return "projects/view";
    }

    @GetMapping("/{id}/manufacturing_details")
    public String viewProjectItems(@PathVariable Long id, Model model) {
        model.addAttribute("items", itemService.findAllByProjectId(id));
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
        return "projects/manufacturing_details";
    }

    @GetMapping("/{id}/documents")
    public String viewProjectDocuments(@PathVariable Long id,
                                       @RequestParam(required = false) String filename,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String lastModify,
                                       @RequestParam(required = false) String responsible,
                                       Model model) {

        // Получаем отфильтрованные документы
        List<DocumentDto> documents = documentService.findFilteredByProjectId(id, filename, status, lastModify, responsible);
        model.addAttribute("docs", documents);

        // Добавляем сам проект для Thymeleaf
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));

        // Сохраняем фильтры для отображения в форме
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("filename", filename);
        searchParams.put("status", status);
        searchParams.put("lastModify", lastModify);
        searchParams.put("responsible", responsible);

        model.addAttribute("searchParams", searchParams);

        return "projects/documents";
    }

    @GetMapping("/{id}/structure")
    public String viewStructureHome(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("structure", itemService.findAllByProjectId(id));
        return "structure/home";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        CadProject project = new CadProject();
        model.addAttribute("project", new CadProject());
        model.addAttribute("users", userRepository.findAllByActive(true));
        model.addAttribute("ignorePatternsText",
                project.getIgnorePatterns() != null
                        ? String.join("\n", project.getIgnorePatterns())
                        : ""
        );
        return "projects/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
        model.addAttribute("users", userRepository.findAllByActive(true));
        CadProject project = projectService.findById(id).orElse(new CadProject());
        model.addAttribute("ignorePatternsText",
                project.getIgnorePatterns() != null
                        ? String.join("\n", project.getIgnorePatterns())
                        : ""
        );
        return "projects/form";
    }

    @PostMapping("/save")
    public String saveProject(@ModelAttribute CadProject project,
                              @RequestParam(name = "ignorePatternsText", required = false) String ignorePatternsText) {

        if (ignorePatternsText != null && !ignorePatternsText.isBlank()) {
            // Разбиваем и по \R (любые переводы строки), и дополнительно на всякий случай по \n
            List<String> patterns = Arrays.stream(ignorePatternsText.split("\\R|\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            project.setIgnorePatterns(patterns);
        } else {
            project.setIgnorePatterns(new ArrayList<>());
        }

        projectService.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return "redirect:/projects";
    }
}