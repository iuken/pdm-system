package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long Id;

    @JoinColumn
    @OneToOne
    private Document document;
//    private String document;

    @JoinColumn
    @OneToOne
    private PdmUser responsible;

    @Column
    @OneToMany
    private List<PdmUser> participants;
//    private String participants;

    @Column
    private String quantity;

    @Column
    private String material;

    @Column
    private String mass;

    @Column
    private String sizes;

    @Column
    private String manufacturer;

    @Column
    private String price;

    @Column
    private Priority priority;
//    private String priority;

    @Column
    private DocumentStatus status;
//    private String status;

    public Item() {
    }

    public Item(Document document) {
        this.document = document;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public PdmUser getResponsible() {
        return responsible;
    }

    public void setResponsible(PdmUser responsible) {
        this.responsible = responsible;
    }

    public List<PdmUser> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PdmUser> participants) {
        this.participants = participants;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
}
