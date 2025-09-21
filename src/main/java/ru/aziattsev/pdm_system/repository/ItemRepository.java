package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.aziattsev.pdm_system.entity.CadProject;
import ru.aziattsev.pdm_system.entity.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByProject(CadProject cadProject);

}
