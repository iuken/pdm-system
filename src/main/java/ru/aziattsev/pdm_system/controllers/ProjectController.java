package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.services.CadProjectService;
import ru.aziattsev.pdm_system.services.ItemService;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final CadProjectService projectService;
    private final ItemService itemService;

    public ProjectController(CadProjectService projectService, ItemService itemService) {
        this.projectService = projectService;
        this.itemService = itemService;
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

    @GetMapping("/{id}/items")
    public String viewProjectItems(@PathVariable Long id, Model model) {
        model.addAttribute("items", itemService.findAllByProjectId(id));
        return "projects/project";
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
        return "projects/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        projectService.findById(id).ifPresent(project -> model.addAttribute("project", project));
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