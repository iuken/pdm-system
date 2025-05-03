package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.services.CadProjectService;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final CadProjectService projectService;

    public ProjectController(CadProjectService projectService) {
        this.projectService = projectService;
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