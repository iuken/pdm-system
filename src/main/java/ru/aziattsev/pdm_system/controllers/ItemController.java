package ru.aziattsev.pdm_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.services.ItemService;

import java.util.List;

@Controller
@RequestMapping("/home")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/update2")
    @ResponseBody
    public ResponseEntity<String> updateItems(@RequestBody List<Item> updatedItems) {
        try {
            itemService.updateAll(updatedItems);
            return ResponseEntity.ok("Изменения сохранены");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка сохранения");
        }
    }

    @PostMapping("/save-items")
    @ResponseBody
    public ResponseEntity<String> saveItems(@RequestBody List<Item> items) {
        itemService.updateAll(items);
        return ResponseEntity.ok("Changes saved successfully");
    }


}