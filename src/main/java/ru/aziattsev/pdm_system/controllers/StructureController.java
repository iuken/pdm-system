package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;

import java.util.List;

@Controller
@RequestMapping("/projects/{id}/structure")
public class StructureController {
    private final EngineeringElementRepository elementRepository;

    public StructureController(EngineeringElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @GetMapping("/assembly_list")
    public String getAssemblyList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("list", elementRepository.findAll());
        return "structure/assembly_list";
    }
}
