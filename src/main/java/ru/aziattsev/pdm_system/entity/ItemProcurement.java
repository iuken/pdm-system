package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Entity
public class ItemProcurement {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    private Double quantity; // сколько заказано/изготовлено

    private ProcurementType type; // MANUFACTURED / PURCHASED

    @ManyToOne
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    private ProcurementStatus status; // ORDERED / IN_PROGRESS / DELIVERED

    private Double unitPrice;

    private LocalDate expectedDeliveryDate;

    private String note;

    public ItemProcurement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public ProcurementType getType() {
        return type;
    }

    public void setType(ProcurementType type) {
        this.type = type;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public ProcurementStatus getStatus() {
        return status;
    }

    public void setStatus(ProcurementStatus status) {
        this.status = status;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}