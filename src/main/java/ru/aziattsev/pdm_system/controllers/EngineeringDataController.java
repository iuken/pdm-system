package ru.aziattsev.pdm_system.controllers;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.ElementParameter;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.entity.XmlTree;
import ru.aziattsev.pdm_system.services.EngineeringDataService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/engineering-data")
public class EngineeringDataController {

    private final EngineeringDataService dataService;

    public EngineeringDataController(EngineeringDataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse> importXmlFile(@RequestParam String filePath) {
        try {
            if (!Files.exists(Path.of(filePath))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Файл не найден по указанному пути: " + filePath));
            }

            if (!filePath.toLowerCase().endsWith(".xml")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Указанный файл не является XML файлом"));
            }

            dataService.importXmlFile(filePath);
            return ResponseEntity.ok()
                    .body(new ApiResponse(true, "Файл успешно импортирован: " + filePath));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Ошибка при импорте файла: " + e.getMessage()));
        }
    }

    @GetMapping("/trees")
    public ResponseEntity<List<XmlTree>> getAllTrees() {
        return ResponseEntity.ok(dataService.getAllTrees());
    }

    @GetMapping("/elements/{treeId}")
    public ResponseEntity<List<EngineeringElement>> getElementsByTree(@PathVariable Long treeId) {
        return ResponseEntity.ok(dataService.getElementsByTree(treeId));
    }

    @GetMapping("/parameters/{objectId}")
    public ResponseEntity<List<ElementParameter>> getElementParameters(@PathVariable String objectId) {
        return ResponseEntity.ok(dataService.getElementParameters(objectId));
    }

    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Геттеры
        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}