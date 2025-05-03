package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parameters")
public class ElementParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "element_id")
    private EngineeringElement element;

    private String name;
    private String synonymName;

    @Column(name = "\"value\"")
    private String value;

    @Column(name = "is_auxiliary")
    private String isAuxiliary;

    @Column(name = "is_generated")
    private String isGenerated;

    @Column(name = "is_user_defined")
    private String isUserDefined;

    private String text;

    @Column(name = "variable_name")
    private String variableName;

    private String units;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EngineeringElement getElement() {
        return element;
    }

    public void setElement(EngineeringElement element) {
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSynonymName() {
        return synonymName;
    }

    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIsAuxiliary() {
        return isAuxiliary;
    }

    public void setIsAuxiliary(String isAuxiliary) {
        this.isAuxiliary = isAuxiliary;
    }

    public String getIsGenerated() {
        return isGenerated;
    }

    public void setIsGenerated(String isGenerated) {
        this.isGenerated = isGenerated;
    }

    public String getIsUserDefined() {
        return isUserDefined;
    }

    public void setIsUserDefined(String isUserDefined) {
        this.isUserDefined = isUserDefined;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    // Геттеры и сеттеры
}
