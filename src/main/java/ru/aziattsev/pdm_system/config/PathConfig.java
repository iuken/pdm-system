package ru.aziattsev.pdm_system.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.aziattsev.pdm_system.services.PathConverter;

@Configuration
public class PathConfig {
    @Value("${path.prefix.server:D:/share/projects/}")
    private String serverPathPrefix;

    @Value("${path.prefix.client:E:/projects/}")
    private String clientPathPrefix;

    @PostConstruct
    public void init() {
        PathConverter.configure(serverPathPrefix, clientPathPrefix);
    }
}