package ru.aziattsev.pdm_system.entity;

import jakarta.persistence.*;

@Entity
@Table
public class DocUser {
    @Column
    private String name;
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public Long getId() {
        return id;
    }
}
