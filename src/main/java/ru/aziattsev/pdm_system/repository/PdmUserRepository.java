package ru.aziattsev.pdm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aziattsev.pdm_system.entity.PdmUser;

import java.util.Optional;

public interface PdmUserRepository extends JpaRepository<PdmUser, Long> {
    Optional<PdmUser> findByUsername(String username);
}