package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aziattsev.pdm_system.entity.ElementParameter;
import ru.aziattsev.pdm_system.entity.EngineeringElement;

import java.util.List;

public interface ElementParameterRepository extends JpaRepository<ElementParameter, Long> {
    List<ElementParameter> findByElementObjectId(String objectId);

    void deleteByElement(EngineeringElement element);
}