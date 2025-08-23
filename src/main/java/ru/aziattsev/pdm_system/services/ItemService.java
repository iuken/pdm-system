package ru.aziattsev.pdm_system.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Document;
import ru.aziattsev.pdm_system.entity.Item;
import ru.aziattsev.pdm_system.repository.CadProjectRepository;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;
import ru.aziattsev.pdm_system.repository.ItemRepository;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    public List<Item> findAllByProjectIdWithExistedDocument(Long id) {
        CadProject cadProject = cadProjectRepository.getReferenceById(id);

        List<Item> items = itemRepository.findByProjectAndDocumentIsExistTrue(cadProject);

        // компилируем regex-паттерны, но игнорируем битые
        List<Pattern> ignorePatterns = cadProject.getIgnorePatterns().stream()
                .map(p -> {
                    try {
                        return Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                    } catch (PatternSyntaxException e) {
                        //log.warn("Некорректный regex в проекте {}: '{}'", cadProject.getId(), p);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return items.stream()
                .filter(item -> {
                    Document doc = item.getDocument();
                    if (doc == null || doc.getFilePath() == null) return false;

                    String filePath = doc.getFilePath().replace("\\", "/");

                    // исключаем документ, если он совпадает хотя бы с одним regex
                    return ignorePatterns.stream().noneMatch(p -> p.matcher(filePath).matches());
                })
                .toList();
    }


    public void updateFromProjectStructure() {
        List<Item> items = itemRepository.findAll();

        for (Item item : items) {
            // Суммируем quantity всех связанных EngineeringElement
            Double totalQuantity = elementRepository.sumQuantityByItem(item);

            // Обновляем quantity в Item
            if (totalQuantity != null) {
                item.setQuantity(totalQuantity);
            } else {
                item.setQuantity(0d); // или другое значение по умолчанию
            }
        }

        // Сохраняем все обновленные Items
        itemRepository.saveAll(items);
    }

    public List<Item> findFilteredByProjectId(Long projectId,
                                              String filename,
                                              String status,
                                              String lastModify,
                                              String responsible) {
        // Сначала получаем все документы проекта
        List<Item> items = findAllByProjectIdWithExistedDocument(projectId);

        // Фильтруем по введенным параметрам с игнорированием регистра
        return items.stream()
                .filter(item -> filename == null || filename.isBlank()
                        || (item.getDocument() != null
                        && item.getDocument().getClientFilePath() != null
                        && item.getDocument().getClientFilePath().toLowerCase()
                        .contains(filename.toLowerCase())))
                .filter(item -> status == null || status.isBlank()
                        || (item.getStatus() != null
                        && item.getStatus().getDisplayName() != null
                        && item.getStatus().getDisplayName().toLowerCase()
                        .contains(status.toLowerCase())))
                .filter(item -> lastModify == null || lastModify.isBlank()
                        || (item.getLastModify() != null
                        && item.getLastModify().getDisplayName() != null
                        && item.getLastModify().getDisplayName().toLowerCase()
                        .contains(lastModify.toLowerCase())))
                .filter(item -> responsible == null || responsible.isBlank()
                        || (item.getResponsible() != null
                        && item.getResponsible().getDisplayName() != null
                        && item.getResponsible().getDisplayName().toLowerCase()
                        .contains(responsible.toLowerCase())))
                .toList();
    }
}