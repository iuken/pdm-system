package ru.aziattsev.pdm_system.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

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

}