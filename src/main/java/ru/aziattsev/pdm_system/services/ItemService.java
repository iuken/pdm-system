package ru.aziattsev.pdm_system.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;
import ru.aziattsev.pdm_system.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {


    private final ItemRepository itemRepository;
    private final CadProjectRepository cadProjectRepository;

    private final EngineeringElementRepository elementRepository;

    public ItemService(ItemRepository itemRepository,
                       CadProjectRepository cadProjectRepository,
                       EngineeringElementRepository elementRepository) {
        this.itemRepository = itemRepository;
        this.cadProjectRepository = cadProjectRepository;
        this.elementRepository = elementRepository;
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

    public void updateFromProjectStructure(){
        List<Item> items = itemRepository.findAll();

        for (Item item : items) {
            // Суммируем quantity всех связанных EngineeringElement
            Integer totalQuantity = elementRepository.sumQuantityByItem(item);

            // Обновляем quantity в Item
            if (totalQuantity != null) {
                item.setQuantity(String.valueOf(totalQuantity));
            } else {
                item.setQuantity("0"); // или другое значение по умолчанию
            }
        }

        // Сохраняем все обновленные Items
        itemRepository.saveAll(items);
    }
}