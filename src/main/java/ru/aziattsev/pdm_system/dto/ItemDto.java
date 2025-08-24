package ru.aziattsev.pdm_system.dto;

import ru.aziattsev.pdm_system.entity.DocumentStatus;
import ru.aziattsev.pdm_system.services.PathConverter;

public class ItemDto {
    private Long id;
    private String clientFilePath;
    private DocumentStatus status;
    private String lastModifyDisplayName;
    private String responsibleDisplayName;

    private String statusName;

    public ItemDto(Long id, String clientFilePath, DocumentStatus status, String lastModifyDisplayName, String responsibleDisplayName) {
        this.id = id;
        this.clientFilePath = clientFilePath;
        this.status = status;
        this.lastModifyDisplayName = lastModifyDisplayName != null ? lastModifyDisplayName : "Не указан";
        this.responsibleDisplayName = responsibleDisplayName != null ? responsibleDisplayName : "Не указан";
        this.statusName = status != null ? status.name() : "UNDEFINED"; // новое поле для JS
    }
    public Long getId() { return id; }
    public String getClientFilePath() { return clientFilePath; }
    public DocumentStatus getStatus() { return status; }
    public String getStatusDisplayName() { return status != null ? status.getDisplayName() : "Не указан"; }
    public String getLastModifyDisplayName() { return lastModifyDisplayName; }
    public String getResponsibleDisplayName() { return responsibleDisplayName; }
    public String getStatusName() { return statusName; }
}