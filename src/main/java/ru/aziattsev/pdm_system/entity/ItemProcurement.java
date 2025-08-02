package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Entity
public class ItemProcurement {
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
}