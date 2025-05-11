package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "elements")
public class EngineeringElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tree_id", nullable = false)
    private XmlTree tree;

    private String objectId;

    @ManyToOne
    @JoinColumn
    private EngineeringElement parent;

    private Integer position;
    private Integer childrenCount;

//    // Основные параметры как поля класса
    private String designation;     // Обозначение
    private String name;           // Наименование
    private String fullDesignation; // Обозначение полное
    private String section;        // Раздел
    private String material;       // Марка материала
    private String unit;           // Единица измерения
    private Integer quantity;      // Количество
    private Double mass;           // Масса
    private String format;         // Формат

    @ManyToOne
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public XmlTree getTree() {
        return tree;
    }

    public void setTree(XmlTree tree) {
        this.tree = tree;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public EngineeringElement getParent() {
        return parent;
    }

    public void setParent(EngineeringElement parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullDesignation() {
        return fullDesignation;
    }

    public void setFullDesignation(String fullDesignation) {
        this.fullDesignation = fullDesignation;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

