package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.aziattsev.pdm_system.services.PathConverter;

import java.util.Date;

@Entity
@Table
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    private String filePath;

    @Column
    private String designation;

    @Column
    private String name;

    @Column
    private String modelMaker;

    @Column
    private String drawing;
    @Column
    private String checking;

    @Column
    private String standardControl;

    @Column
    private String technicalControl;

    @Column
    private String approved;

    @Column
    private Date creationTime;
    @Column
    private Date lastModifiedTime;

    @Column
    private String material2;
    @Column
    private String material3;
    @Column
    private String material4;
    @Column
    private String mass;
    @Column
    private String xSize;
    @Column
    private String ySize;
    @Column
    private String zSize;


    @JoinColumn
    @ManyToOne
    private CadProject project;

    public Document(String filePath, Date creationTime, Date lastModifiedTime) {
        this.filePath = filePath;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
    }

    public Document(String filePath) {
        this.filePath = filePath;
    }

    public Document() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }
    public String getClientFilePath() {
        return PathConverter.toClientPath(filePath);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelMaker() {
        return modelMaker;
    }

    public void setModelMaker(String modelMaker) {
        this.modelMaker = modelMaker;
    }

    public String getDrawing() {
        return drawing;
    }

    public void setDrawing(String drawing) {
        this.drawing = drawing;
    }

    public String getStandardControl() {
        return standardControl;
    }

    public void setStandardControl(String standardControl) {
        this.standardControl = standardControl;
    }

    public String getTechnicalControl() {
        return technicalControl;
    }

    public void setTechnicalControl(String technicalControl) {
        this.technicalControl = technicalControl;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getChecking() {
        return checking;
    }

    public void setChecking(String checking) {
        this.checking = checking;
    }

    public String getMaterial2() {
        return material2;
    }

    public void setMaterial2(String material2) {
        this.material2 = material2;
    }

    public String getMaterial3() {
        return material3;
    }

    public void setMaterial3(String material3) {
        this.material3 = material3;
    }

    public String getMaterial4() {
        return material4;
    }

    public void setMaterial4(String material4) {
        this.material4 = material4;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }

    public String getxSize() {
        return xSize;
    }

    public void setxSize(String xSize) {
        this.xSize = xSize;
    }

    public String getySize() {
        return ySize;
    }

    public void setySize(String ySize) {
        this.ySize = ySize;
    }

    public String getzSize() {
        return zSize;
    }

    public void setzSize(String zSize) {
        this.zSize = zSize;
    }

    public CadProject getProject() {
        return project;
    }

    public void setProject(CadProject project) {
        this.project = project;
    }
}
