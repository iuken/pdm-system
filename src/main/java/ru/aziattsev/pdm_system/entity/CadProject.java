package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

@Entity
@Table
public class CadProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    private String info;
    // Храним только серверные пути в БД
    private String directory;
    private String mainAssemblyPath;
    private String structurePath;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String serverDirectory) {
        this.directory = serverDirectory;
    }


    public String getMainAssemblyPath() {
        return mainAssemblyPath;
    }

    public void setMainAssemblyPath(String serverMainAssemblyPath) {
        this.mainAssemblyPath = serverMainAssemblyPath;
    }

    public String getStructurePath() {
        return structurePath;
    }

    public void setStructurePath(String serverStructurePath) {
        this.structurePath = serverStructurePath;
    }

}
