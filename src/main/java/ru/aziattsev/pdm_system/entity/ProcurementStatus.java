package ru.aziattsev.pdm_system.entity;

public enum ProcurementStatus {
    NOT_PLANNED,       // Ещё не запланировано
    PLANNED,           // Запланировано, но не утверждено
    ORDERED,           // Заказано (или в производстве)
    PARTIALLY_DELIVERED, // Частично поставлено
    DELIVERED,         // Полностью поставлено
    CANCELLED,         // Отменено
    ERROR              // Ошибка или сбой (например, брак, нет поставщика)
}