package ru.aziattsev.pdm_system.entity;

public enum ProcurementType {
    PURCHASED,         // Закупается у поставщика
    OUTSOURCED,        // Изготавливается на стороне (аутсорс)
    CUSTOMER_SUPPLIED, // Поставляется заказчиком
    NOT_APPLICABLE     // Не требуется (например, виртуальный элемент)
}