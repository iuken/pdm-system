package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;
import ru.aziattsev.pdm_system.services.CadProjectService;
import ru.aziattsev.pdm_system.services.ItemService;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final CadProjectService projectService;
    private final ItemService itemService;
    private final PdmUserRepository userRepository;

    public ProjectController(CadProjectService projectService, ItemService itemService, PdmUserRepository userRepository) {
        this.projectService = projectService;
        this.itemService = itemService;
        this.userRepository = userRepository;
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
    public String viewProjectDocuments(@PathVariable Long id, Model model) {
        model.addAttribute("items", itemService.findAllByProjectId(id));
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
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
        model.addAttribute("project", new CadProject());
        model.addAttribute("users", userRepository.findAllByActive(true));
        return "projects/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
        model.addAttribute("users", userRepository.findAllByActive(true));
        return "projects/form";
    }

    @PostMapping("/save")
    public String saveProject(@ModelAttribute CadProject project) {
        projectService.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return "redirect:/projects";
    }
}