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

    @JoinColumn
    @ManyToOne
    private Document document;


    @Column
    @OneToMany
    private List<PdmUser> participants;

//    private String participants;

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

    @Column
    private Priority priority;

//    private String priority;

    @Column
    private DocumentStatus status;
//    private String status;

    @JoinColumn
    @ManyToOne
    private CadProject project;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemProcurement> procurements;

    @JoinColumn
    @ManyToOne
    private PdmUser responsible;

    @JoinColumn
    @ManyToOne
    private PdmUser lastModify;

    @JoinColumn
    @ManyToOne
    private PdmUser developer;

    public Item() {
        this.setStatus(DocumentStatus.UNDEFINED);
    }
    public Item(Document document) {
        this.project = document.getProject();
        this.document = document;
        this.price = 0d;
        this.quantity = 0d;
        this.setStatus(DocumentStatus.UNDEFINED);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
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
    public PdmUser getResponsible() {
        return responsible;
    }

    public void setResponsible(PdmUser responsible) {
        this.responsible = responsible;
    }
    public PdmUser getLastModify() {
        return lastModify;
    }

    public void setLastModify(PdmUser lastModify) {
        this.lastModify = lastModify;
    }

    public PdmUser getDeveloper() {
        return developer;
    }

    public void setDeveloper(PdmUser developer) {
        this.developer = developer;
    }
}
