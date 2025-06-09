package ru.aziattsev.pdm_system.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;
import ru.aziattsev.pdm_system.services.EngineeringDataService;
import ru.aziattsev.pdm_system.services.EngineeringElementService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects/{id}/structure")
public class StructureController {
    private final EngineeringElementRepository elementRepository;
    private final CadProjectRepository cadProjectRepository;
    private final EngineeringElementService elementService;
    private final EngineeringDataService dataService;


    public StructureController(EngineeringElementRepository elementRepository, CadProjectRepository cadProjectRepository, EngineeringElementService elementService, EngineeringDataService dataService) {
        this.elementRepository = elementRepository;
        this.cadProjectRepository = cadProjectRepository;
        this.elementService = elementService;
        this.dataService = dataService;
    }



    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<ApiResponse> importXmlFile(@PathVariable("id") Long projectId) {
        try {
            // 1. Получаем проект из репозитория
            CadProject project = cadProjectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("Проект с ID " + projectId + " не найден"));

            // 2. Получаем путь к файлу из проекта
            String filePath = project.getStructurePath();

            // 3. Проверяем, что путь указан
            if (filePath == null || filePath.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Для проекта не указан путь к XML файлу структуры"));
            }

            // 4. Проверяем существование файла
            if (!Files.exists(Path.of(filePath))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Файл не найден по указанному пути: " + filePath));
            }

            // 5. Проверяем расширение файла
            if (!filePath.toLowerCase().endsWith(".xml")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Указанный файл не является XML файлом"));
            }

            // 6. Выполняем импорт
            dataService.importXmlFile(filePath, projectId);

            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Данные успешно обновлены из файла: " + filePath));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Ошибка при импорте данных: " + e.getMessage()));
        }
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

    @GetMapping("/check1")
    public String findBySameDesignationAndDifferentNames(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "ДСЕ с одинаковым обозначением и разным наименованием");
        model.addAttribute("list", elementService.findBySameDesignationAndDifferentNames(id));
        return "structure/list";
    }

    @GetMapping("/check2")
    public String findUnusedAssemblyUnits(@PathVariable Long id, Model model) {
        model.addAttribute("projectId", id);
        model.addAttribute("showSpecSection", true);
        model.addAttribute("showParentStructure", true);
        model.addAttribute("title", "Сборки без вложенных элементов");
        model.addAttribute("list", elementService.findUnusedAssemblyUnits(id));
        return "structure/list";
    }



    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Геттеры
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }

        // Сеттеры (если нужны)
        public void setSuccess(boolean success) { this.success = success; }
        public void setMessage(String message) { this.message = message; }
    }

}
