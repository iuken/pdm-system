package ru.aziattsev.pdm_system.config;

import ru.aziattsev.pdm_system.entity.PdmUser;
import ru.aziattsev.pdm_system.entity.UserRole;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    private final PdmUserRepository pdmUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PdmUserRepository pdmUserRepository, PasswordEncoder passwordEncoder) {
        this.pdmUserRepository = pdmUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (pdmUserRepository.count() == 0) {
            createUser("admin", "admin123", "Администратор Системы", UserRole.ADMIN, UserRole.USER);
            createUser("user1", "user1", "Обычный Пользователь1", UserRole.USER);
            createUser("user2", "user2", "Обычный Пользователь2", UserRole.USER);
            createUser("user3", "user3", "Обычный Пользователь3", UserRole.USER);
            createUser("user4", "user4", "Обычный Пользователь4", UserRole.USER);
            createUser("user5", "user5", "Обычный Пользователь5", UserRole.USER);
            createUser("user6", "user6", "Обычный Пользователь6", UserRole.USER);
            createUser("user7", "user7", "Обычный Пользователь7", UserRole.USER);
            createUser("user8", "user8", "Обычный Пользователь8", UserRole.USER);
            createUser("user9", "user9", "Обычный Пользователь9", UserRole.USER);
            createUser("user10", "user10", "Обычный Пользователь10", UserRole.USER);
            createUser("user11", "user11", "Обычный Пользователь11", UserRole.USER);
            createUser("user12", "user12", "Обычный Пользователь12", UserRole.USER);
            createUser("user13", "user13", "Обычный Пользователь13", UserRole.USER);
            createUser("user14", "user14", "Обычный Пользователь14", UserRole.USER);
            createUser("user15", "user15", "Обычный Пользователь15", UserRole.USER);
            createUser("user16", "user16", "Обычный Пользователь16", UserRole.USER);
            createUser("user17", "user17", "Обычный Пользователь17", UserRole.USER);
            createUser("user18", "user18", "Обычный Пользователь18", UserRole.USER);
            createUser("user19", "user19", "Обычный Пользователь19", UserRole.USER);
            createUser("user20", "user20", "Обычный Пользователь20", UserRole.USER);
        }
    }

    private void createUser(String username, String password, String displayName, UserRole... roles) {
        PdmUser user = new PdmUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setActive(true);
        user.setRoles(Set.of(roles));
        pdmUserRepository.save(user);
    }
}