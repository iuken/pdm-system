package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @OneToMany
    private List<PdmUser> participants;

    @Column
    private Double quantity;
    @Column
    private String material;

    @Column
    private String mass;

    @Column
    private String sizes;

    @Column
    private String manufacturer;

    @Column
    private Double price;

    @JoinColumn
    @ManyToOne
    private CadProject project;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemProcurement> procurements;


    public Item() {
    }

    public Item(Document document) {
        this.project = document.getProject();
        this.price = 0d;
        this.quantity = 0d;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<PdmUser> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PdmUser> participants) {
        this.participants = participants;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CadProject getProject() {
        return project;
    }

    public void setProject(CadProject project) {
        this.project = project;
    }

    public List<ItemProcurement> getProcurements() {
        return procurements;
    }

    public void setProcurements(List<ItemProcurement> procurements) {
        this.procurements = procurements;
    }
}
