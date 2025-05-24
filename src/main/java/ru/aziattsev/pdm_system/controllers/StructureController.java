package ru.aziattsev.pdm_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;
import ru.aziattsev.pdm_system.services.EngineeringElementService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/projects/{id}/structure")
public class StructureController {
    private final EngineeringElementRepository elementRepository;
    private final EngineeringElementService elementService;

    public StructureController(EngineeringElementRepository elementRepository, EngineeringElementService elementService) {
        this.elementRepository = elementRepository;
        this.elementService = elementService;
    }

    @GetMapping("/list")
    public String getList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("title", "Список фрагментов");
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("list", elementService.findAllExcludeSectionWithParent(new ArrayList<>(List.of(
                "Исключаемые разделы")), id));
        return "structure/list";
    }
    @GetMapping("/part_list")
    public String getPartList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("title", "Список деталей");
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", false);
        model.addAttribute("list", elementService.findAllBySection("Спецификации\\Детали", id));
        return "structure/list";
    }
    @GetMapping("/part_list_with_parent")
    public String getPartListWithParent(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("title", "Список деталей");
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("list", elementService.findAllBySectionWithParent("Спецификации\\Детали", id));
        return "structure/list";
    }
    @GetMapping("/goods_list")
    public String getGoodList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", false);
        model.addAttribute("title", "Список прочих изделий");
        model.addAttribute("list", elementService.findAllBySection(new ArrayList<>(List.of(
                "Спецификации\\Прочие изделия",
                "Спецификации\\Материалы")), id));
        return "structure/list";
    }
    @GetMapping("/goods_list_with_parent")
    public String getGoodListWithParent(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "Список прочих изделий");
        model.addAttribute("list", elementService.findAllBySectionWithParent(new ArrayList<>(List.of(
                "Спецификации\\Прочие изделия",
                "Спецификации\\Материалы")), id));
        return "structure/list";
    }
    @GetMapping("/assembly_list")
    public String getAssemblyList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", false);
        model.addAttribute("title", "Список сборок");
        model.addAttribute("list", elementService.findAllBySection("Спецификации\\Сборочные единицы", id));
        return "structure/list";
    }
    @GetMapping("/assembly_list_with_parent")
    public String getAssemblyListWithParent(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "Список сборок");
        model.addAttribute("list", elementService.findAllBySectionWithParent("Спецификации\\Сборочные единицы", id));
        return "structure/list";
    }
    @GetMapping("/standart_parts_list")
    public String getStandardPartList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", false);
        model.addAttribute("title", "Список стандартных изделий");
        model.addAttribute("list", elementService.findAllBySection("Спецификации\\Стандартные изделия", id));
        return "structure/list";
    }
    @GetMapping("/standart_parts_list_with_parent")
    public String getStandardPartListWithParent(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", false);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "Список стандартных изделий");
        model.addAttribute("list", elementService.findAllBySectionWithParent("Спецификации\\Стандартные изделия", id));
        return "structure/list";
    }

    @GetMapping("/other_parts_list")
    public String getOtherPartList(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", false);
        model.addAttribute("title", "Список остальных фрагментов");
        model.addAttribute("list", elementService.findAllExcludeSection(new ArrayList<>(List.of(
                "Спецификации\\Стандартные изделия",
                "Спецификации\\Детали",
                "Спецификации\\Прочие изделия",
                "Спецификации\\Сборочные единицы",
                "Спецификации\\Материалы")), id));
        return "structure/list";
    }

    @GetMapping("/other_parts_list_with_parent")
    public String getOtherPartListWithParent(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "Список остальных фрагментов");
        model.addAttribute("list", elementService.findAllExcludeSectionWithParent(new ArrayList<>(List.of(
                "Спецификации\\Стандартные изделия",
                "Спецификации\\Детали",
                "Спецификации\\Прочие изделия",
                "Спецификации\\Сборочные единицы",
                "Спецификации\\Материалы")), id));
        return "structure/list";
    }



}
