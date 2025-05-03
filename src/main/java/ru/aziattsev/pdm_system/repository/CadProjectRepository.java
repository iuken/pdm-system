package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aziattsev.pdm_system.entity.CadProject;

public interface CadProjectRepository extends JpaRepository<CadProject, Long> {
}
