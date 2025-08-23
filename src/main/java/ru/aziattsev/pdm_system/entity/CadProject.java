package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

//    @JoinColumn
//    @ManyToOne
//    private List<PdmUser> drawing;

    @JoinColumn
    @ManyToOne
    private PdmUser checking;

    @JoinColumn
    @ManyToOne
    private PdmUser standardControl;

    @JoinColumn
    @ManyToOne
    private PdmUser technicalControl;

    @JoinColumn
    @ManyToOne
    private PdmUser approved;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cad_project_ignore_patterns", joinColumns = @JoinColumn(name = "cad_project_id"))
    @Column(name = "pattern")
    private List<String> ignorePatterns = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public PdmUser getChecking() {
        return checking;
    }

    public void setChecking(PdmUser checking) {
        this.checking = checking;
    }

    public PdmUser getStandardControl() {
        return standardControl;
    }

    public void setStandardControl(PdmUser standardControl) {
        this.standardControl = standardControl;
    }

    public PdmUser getTechnicalControl() {
        return technicalControl;
    }

    public void setTechnicalControl(PdmUser technicalControl) {
        this.technicalControl = technicalControl;
    }

    public PdmUser getApproved() {
        return approved;
    }

    public void setApproved(PdmUser approved) {
        this.approved = approved;
    }

    public List<String> getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(List<String> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

}
