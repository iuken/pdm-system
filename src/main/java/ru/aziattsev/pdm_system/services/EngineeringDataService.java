package ru.aziattsev.pdm_system.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.aziattsev.pdm_system.entity.ElementParameter;
import ru.aziattsev.pdm_system.entity.EngineeringElement;
import ru.aziattsev.pdm_system.entity.XmlTree;
import ru.aziattsev.pdm_system.repository.ElementParameterRepository;
import ru.aziattsev.pdm_system.repository.EngineeringElementRepository;
import ru.aziattsev.pdm_system.repository.XmlTreeRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class EngineeringDataService {

    private final XmlTreeRepository xmlTreeRepository;
    private final EngineeringElementRepository elementRepository;
    private final ElementParameterRepository parameterRepository;

    public EngineeringDataService(XmlTreeRepository xmlTreeRepository,
                                  EngineeringElementRepository elementRepository,
                                  ElementParameterRepository parameterRepository) {
        this.xmlTreeRepository = xmlTreeRepository;
        this.elementRepository = elementRepository;
        this.parameterRepository = parameterRepository;
    }

    public void importXmlFile(String filePath) throws Exception {
        File xmlFile = new File(filePath);
        String sourceName = xmlFile.getName();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList rootElements = doc.getElementsByTagName("Elements").item(0).getChildNodes();

        for (int i = 0; i < rootElements.getLength(); i++) {
            Node node = rootElements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("Element")) {
                Element element = (Element) node;
                String rootObjectId = element.getAttribute("ObjectId");

                // Создаем или получаем существующее дерево
                XmlTree tree = xmlTreeRepository.findBySourceNameAndRootObjectId(sourceName, rootObjectId)
                        .orElseGet(() -> {
                            XmlTree newTree = new XmlTree();
                            newTree.setSourceName(sourceName);
                            newTree.setRootObjectId(rootObjectId);
                            return xmlTreeRepository.save(newTree);
                        });

                // Парсим дерево элементов
                parseElement(element, null, tree);
            }
        }
    }

    private void parseElement(Element xmlElement, EngineeringElement parent, XmlTree tree) {
        String objectId = xmlElement.getAttribute("ObjectId");

        // Создаем или получаем элемент
        EngineeringElement element = elementRepository.findByTreeAndObjectId(tree, objectId)
                .orElseGet(() -> {
                    EngineeringElement newElement = new EngineeringElement();
                    newElement.setTree(tree);
                    newElement.setObjectId(objectId);
                    newElement.setParent(parent);
                    return newElement;
                });

        // Парсим параметры из XML
        List<ElementParameter> parameters = new ArrayList<>();
        NodeList params = xmlElement.getElementsByTagName("Parameter");
        for (int i = 0; i < params.getLength(); i++) {
            Node paramNode = params.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paramElement = (Element) paramNode;
                ElementParameter param = new ElementParameter();
                param.setElement(element);
                param.setName(paramElement.getAttribute("Name"));
                param.setValue(paramElement.getAttribute("Value"));
                param.setIsAuxiliary(paramElement.getAttribute("IsAuxiliary"));
                param.setIsGenerated(paramElement.getAttribute("IsGenerated"));
                param.setIsUserDefined(paramElement.getAttribute("IsUserDefined"));
                param.setText(paramElement.getAttribute("Text"));
                param.setVariableName(paramElement.getAttribute("VariableName"));
                param.setUnits(paramElement.getAttribute("Units"));
                parameters.add(param);
            }
        }

        // Применяем параметры к полям элемента
        applyParametersToElement(element, parameters);

        // Сохраняем элемент с параметрами
        element = elementRepository.save(element);
        parameterRepository.saveAll(parameters);

        // Обрабатываем дочерние элементы
        NodeList children = xmlElement.getElementsByTagName("Children");
        if (children.getLength() > 0) {
            NodeList childElements = ((Element) children.item(0)).getElementsByTagName("Element");
            element.setChildrenCount(childElements.getLength());
            elementRepository.save(element);

            for (int i = 0; i < childElements.getLength(); i++) {
                Node childNode = childElements.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    parseElement((Element) childNode, element, tree);
                }
            }
        }
    }

    private int getPositionFromXml(Element xmlElement) {
        NodeList params = xmlElement.getElementsByTagName("Parameter");
        for (int i = 0; i < params.getLength(); i++) {
            Node paramNode = params.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paramElement = (Element) paramNode;
                if (paramElement.getAttribute("Name").equals("Position")) {
                    return Integer.parseInt(paramElement.getAttribute("Value"));
                }
            }
        }
        return 0;
    }

    private void processParameters(Element xmlElement, EngineeringElement element) {
        // Удаляем старые параметры
        parameterRepository.deleteByElement(element);

        // Добавляем новые параметры
        NodeList params = xmlElement.getElementsByTagName("Parameter");
        for (int i = 0; i < params.getLength(); i++) {
            Node paramNode = params.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paramElement = (Element) paramNode;

                ElementParameter param = new ElementParameter();
                param.setElement(element);
                param.setName(paramElement.getAttribute("Name"));
                param.setSynonymName(paramElement.getAttribute("SynonymName"));
                param.setValue(paramElement.getAttribute("Value"));
                param.setIsAuxiliary(paramElement.getAttribute("IsAuxiliary"));
                param.setIsGenerated(paramElement.getAttribute("IsGenerated"));
                param.setIsUserDefined(paramElement.getAttribute("IsUserDefined"));
                param.setText(paramElement.getAttribute("Text"));
                param.setVariableName(paramElement.getAttribute("VariableName"));
                param.setUnits(paramElement.getAttribute("Units"));

                parameterRepository.save(param);
            }
        }
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
                    ? Integer.parseInt(paramMap.get("Количество"))
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
    private Integer parseIntSafe(String value) {
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleSafe(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<ElementParameter> processXmlParameters(Element xmlElement, EngineeringElement element) {
        // Удаляем старые параметры
        parameterRepository.deleteByElement(element);

        // Обрабатываем параметры из XML
        List<ElementParameter> parameters = new ArrayList<>();
        NodeList params = xmlElement.getElementsByTagName("Parameter");

        for (int i = 0; i < params.getLength(); i++) {
            Node paramNode = params.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paramElement = (Element) paramNode;

                ElementParameter param = new ElementParameter();
                param.setElement(element);
                param.setName(paramElement.getAttribute("Name"));
                param.setValue(paramElement.getAttribute("Value"));
                // ... другие поля параметра

                parameters.add(parameterRepository.save(param));
            }
        }

        return parameters;
    }

    private void processChildElements(Element xmlElement, EngineeringElement parent, XmlTree tree) {
        NodeList children = xmlElement.getElementsByTagName("Children");
        if (children.getLength() > 0) {
            NodeList childElements = ((Element) children.item(0)).getElementsByTagName("Element");
            parent.setChildrenCount(childElements.getLength());
            parent = elementRepository.save(parent);

            for (int i = 0; i < childElements.getLength(); i++) {
                Node childNode = childElements.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    parseElement((Element) childNode, parent, tree);
                }
            }
        }
    }
}