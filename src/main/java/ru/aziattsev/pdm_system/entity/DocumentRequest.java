package ru.aziattsev.pdm_system.entity;

public record DocumentRequest(

        String filePath,
        String designation,
        String name,
        String modelMaker,
        String drawing,
        String checking,
        String standardControl,
        String technicalControl,
        String approved,
        String material2,
        String material3,
        String material4,
        String mass,
        String xSize,
        String ySize,
        String zSize,
        DocumentStatus documentStatus,
        String user
) {
}