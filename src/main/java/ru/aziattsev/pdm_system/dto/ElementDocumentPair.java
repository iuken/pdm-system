package ru.aziattsev.pdm_system.dto;

import ru.aziattsev.pdm_system.entity.Document;
import ru.aziattsev.pdm_system.entity.EngineeringElement;

public record  ElementDocumentPair (
        EngineeringElement element,
        Document document
) {}