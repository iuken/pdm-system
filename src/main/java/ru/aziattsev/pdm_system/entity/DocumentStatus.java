package ru.aziattsev.pdm_system.entity;

public enum DocumentStatus {
    UNDEFINED("Не определен"),
    MARKED_AS_READY("Разраб.: готово"),
    CHECKER_MARKED_AS_NOT_READY("Пров.: не готово"),
    CHECKER_MARKED_AS_READY("Пров.: готово"),
    STANDARD_CONTROL_MARKED_AS_NOT_READY("Н.Контр.: не готово"),
    STANDARD_CONTROL_MARKED_AS_READY("Н.Контр.: готово"),
    TECHNICAL_CONTROL_MARKED_AS_NOT_READY("Т.Контр.: не готово"),
    TECHNICAL_CONTROL_MARKED_AS_READY("Т.Контр.: готово"),
    APPROVED_MARKED_AS_NOT_READY("Утв.: не готово"),
    APPROVED_MARKED_AS_READY("Утв.: готово");

    private final String displayName;

    DocumentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}