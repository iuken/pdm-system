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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        EngineeringElement engineeringElement = new EngineeringElement();
        engineeringElement.setTree(tree);
        engineeringElement.setObjectId(objectId);
        engineeringElement.setParent(parent);
        // Парсим параметры из XML
        List<ElementParameter> parameters = new ArrayList<>();
        NodeList params = xmlElement.getChildNodes();
        Node parametersNode = null;
        for (int i = 0; i < params.getLength(); i++) {
            Node paramNode = params.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE
                    && paramNode.getNodeName().equals("Parameters")) {
                parametersNode = params.item(i);
            }
        }
        NodeList parametersList = parametersNode.getChildNodes();
        for (int i = 0; i < parametersList.getLength(); i++) {
            Node paramNode = parametersList.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                Element paramElement = (Element) paramNode;
                ElementParameter param = new ElementParameter();
                param.setElement(engineeringElement);
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
        applyParametersToElement(engineeringElement, parameters);

        // Сохраняем элемент с параметрами
        elementRepository.save(engineeringElement);
        parameterRepository.saveAll(parameters);

        // Обрабатываем дочерние элементы
        NodeList children = xmlElement.getElementsByTagName("Children");
        if (children.getLength() > 0) {
            NodeList childElements = ((Element) children.item(0)).getElementsByTagName("Element");
            engineeringElement.setChildrenCount(childElements.getLength());
            elementRepository.save(engineeringElement);

            for (int i = 0; i < childElements.getLength(); i++) {
                Node childNode = childElements.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    parseElement((Element) childNode, engineeringElement, tree);
                }
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
}