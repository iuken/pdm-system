package ru.aziattsev.pdm_system.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aziattsev.pdm_system.entity.*;
import ru.aziattsev.pdm_system.repository.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EngineeringDataService {

    private final XmlTreeRepository xmlTreeRepository;
    private final EngineeringElementRepository elementRepository;
    private final ElementParameterRepository parameterRepository;
    private final ItemRepository itemRepository;

    private final DocumentRepository documentRepository;

    private final CadProjectRepository cadProjectRepository;

    public EngineeringDataService(XmlTreeRepository xmlTreeRepository,
                                  EngineeringElementRepository elementRepository,
                                  ElementParameterRepository parameterRepository,
                                  ItemRepository itemRepository,
                                  DocumentRepository documentRepository, CadProjectRepository cadProjectRepository) {
        this.xmlTreeRepository = xmlTreeRepository;
        this.elementRepository = elementRepository;
        this.parameterRepository = parameterRepository;
        this.itemRepository = itemRepository;
        this.documentRepository = documentRepository;
        this.cadProjectRepository = cadProjectRepository;
    }

    public void importXmlFile(String filePath, Long projectId) throws Exception {
        CadProject cadProject = cadProjectRepository.getReferenceById(projectId);
        XmlTree newTree = new XmlTree();
        xmlTreeRepository.save(newTree);

        File xmlFile = new File(filePath);

        try {
            Document doc = Jsoup.parse(xmlFile, null, "", Parser.xmlParser());
            org.jsoup.nodes.Element rootElement = doc.selectFirst("Element");
            parseElement(rootElement, null, newTree, cadProject);
        } catch (IOException e) {

        }
    }

    private void parseElement(Element xmlElement, EngineeringElement parent, XmlTree tree, CadProject cadProject) {
        String objectId = xmlElement.attributes().get("ObjectId");

        EngineeringElement engineeringElement = new EngineeringElement();
        engineeringElement.setTree(tree);
        engineeringElement.setObjectId(objectId);
        engineeringElement.setParent(parent);
        // Парсим параметры из XML
        List<ElementParameter> parameters;

        Elements xmlParameters = xmlElement.selectFirst("Parameters").select("Parameter");
        Element xmlChildren = xmlElement.selectFirst("Children");

        parameters = parseParameter(xmlParameters);
        applyParametersToElement(engineeringElement, parameters);
        engineeringElement.setCadProject(cadProject);
        elementRepository.save(engineeringElement);

        if (xmlChildren != null) {

            Element firstChild = xmlChildren.selectFirst("Element");
            parseElement(firstChild, engineeringElement, tree, cadProject);
            for (Element child : xmlChildren.selectFirst("Element").siblingElements())
                parseElement(child, engineeringElement, tree, cadProject);
        }

    }

    List<ElementParameter> parseParameter(Elements xmlParameters) {
        List<ElementParameter> params = new ArrayList<>();

        int i = 0;
        for (Element parameter : xmlParameters) {
            ElementParameter param = new ElementParameter();
            for (Attribute attribute : parameter.attributes()) {
                switch (attribute.getKey()) {
                    case "Name":
                        param.setName(attribute.getValue());
                        break;
                    case "IsAuxiliary":
                        param.setIsAuxiliary(attribute.getValue());
                        break;
                    case "SynonymName":
                        param.setSynonymName(attribute.getValue());
                        break;
                    case "Value":
                        param.setValue(attribute.getValue());
                        break;
                    case "Units":
                        param.setUnits(attribute.getValue());
                        break;
                    case "IsGenerated":
                        param.setIsGenerated(attribute.getValue());
                        break;
                    case "IsUserDefined":
                        param.setIsUserDefined(attribute.getValue());
                        break;
                    case "Text":
                        param.setText(attribute.getValue());
                        break;
                    case "VariableName":
                        param.setVariableName(attribute.getValue());
                        break;
                    default:
                        break;
                }
            }
            params.add(param);
            i++;
        }

        return params;
    }

    public List<XmlTree> getAllTrees() {
        return xmlTreeRepository.findAll();
    }

    public List<EngineeringElement> getElementsByTree(Long treeId) {
        return elementRepository.findByTreeId(treeId);
    }

    public List<ElementParameter> getElementParameters(String objectId) {
        return parameterRepository.findByElementObjectId(objectId);
    }

    private void applyParametersToElement(EngineeringElement element, List<ElementParameter> parameters) {
        Map<String, String> paramMap = parameters.stream()
                .collect(Collectors.toMap(
                        ElementParameter::getName,
                        ElementParameter::getValue,
                        (oldValue, newValue) -> newValue));

        // Устанавливаем основные поля
        element.setDesignation(paramMap.getOrDefault("Обозначение", null));
        element.setName(paramMap.getOrDefault("Наименование", null));
        element.setFullDesignation(paramMap.getOrDefault("Обозначение полное", null));
        element.setSection(paramMap.getOrDefault("Раздел", null));
        element.setMaterial(paramMap.getOrDefault("Марка материала", null));
        element.setUnit(paramMap.getOrDefault("Единица измерения", null));

        // Обрабатываем числовые значения
        try {
            element.setQuantity(paramMap.containsKey("Количество")
                    ? Double.parseDouble(paramMap.get("Количество"))
                    : null);
        } catch (NumberFormatException e) {
            element.setQuantity(null);
        }

        try {
            element.setMass(paramMap.containsKey("Масса")
                    ? Double.parseDouble(paramMap.get("Масса"))
                    : null);
        } catch (NumberFormatException e) {
            element.setMass(null);
        }

        element.setFormat(paramMap.getOrDefault("Формат", null));
    }

    public void linkElementsToItems() {
        List<EngineeringElement> elements = elementRepository.findAll();
        for (EngineeringElement element : elements) {
            // Ищем подходящий Document
            Optional<ru.aziattsev.pdm_system.entity.Document> matchingDocument = documentRepository
                    .findByDesignationAndName(element.getDesignation(), element.getName());

            if (matchingDocument.isPresent()) {
                // Ищем или создаем Item для этого Document
                Item item = itemRepository.findByDocument(matchingDocument.get())
                        .orElseGet(() -> {
                            Item newItem = new Item();
                            newItem.setDocument(matchingDocument.get());
                            newItem.setQuantity(element.getQuantity() != null ?
                                    element.getQuantity().toString() : "0");
                            return itemRepository.save(newItem);
                        });

                // Привязываем Item к элементу
                element.setItem(item);
                elementRepository.save(element);
            }
        }
    }
}