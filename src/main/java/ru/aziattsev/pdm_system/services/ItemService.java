package ru.aziattsev.pdm_system.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;
import ru.aziattsev.pdm_system.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {


    private final ItemRepository itemRepository;
    private final CadProjectRepository cadProjectRepository;

    public ItemService(ItemRepository itemRepository, CadProjectRepository cadProjectRepository) {
        this.itemRepository = itemRepository;
        this.cadProjectRepository = cadProjectRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public void updateAll(List<Item> items) {
        items.forEach(item -> {
            Item existing = itemRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            existing.setManufacturer(item.getManufacturer());
            existing.setQuantity(item.getQuantity());
            existing.setPrice(item.getPrice());
            existing.setStatus(item.getStatus());
            itemRepository.save(existing);
        });
    }

    public List<Item> findAllByProjectId(Long id) {
        CadProject cadProject = cadProjectRepository.getReferenceById(id);
        return itemRepository.findAllByProject(cadProject);
    }
}