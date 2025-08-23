package ru.aziattsev.pdm_system.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.aziattsev.pdm_system.entity.DocumentRequest;
import ru.aziattsev.pdm_system.entity.PdmUser;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;
import ru.aziattsev.pdm_system.services.DocumentService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AppController {
    @Autowired

    DocumentService documentService;
    @Autowired
    PdmUserRepository pdmUserRepository;

    @Value("${app.version}")
    private String appVersion;
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }


    @PostMapping("/api/update")
    public void updateDocumentInfo2(@Valid @RequestBody DocumentRequest request) {
        documentService.updateFromCad(request);

    }

        @GetMapping("/home")
        public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
            if (userDetails != null) {
                PdmUser user = pdmUserRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                model.addAttribute("pdmUser", user);
            }
            // Получаем аутентификационные данные текущего пользователя
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Добавляем имя пользователя в модель
            String displayName = authentication.getName(); // или получить из вашего PdmUser

            // Добавляем дату последнего входа (можно получать из БД)
            model.addAttribute("lastLogin", LocalDateTime.now());

            // Заглушки для демонстрации (в реальном приложении получать из сервисов)
            model.addAttribute("projectsCount", 5);
            model.addAttribute("tasksCount", 3);
            model.addAttribute("unreadNotifications", 2);

            // Пример последних действий
            model.addAttribute("recentActivities", List.of(
                    new Activity("Создал проект 'Модуль А'", LocalDateTime.now().minusHours(2)),
                    new Activity("Изменен документ 'Чертеж-001'", LocalDateTime.now().minusDays(1)),
                    new Activity("Утверждена спецификация", LocalDateTime.now().minusDays(2))
            ));
            model.addAttribute("version", appVersion);
            return "home"; // имя шаблона Thymeleaf (home.html)
        }



        @GetMapping("/home2")
    public String home2(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            PdmUser user = pdmUserRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            model.addAttribute("pdmUser", user);
        }
        return "home";
    }

    public static class Activity {
        private String description;
        private LocalDateTime date;

        public Activity(String description, LocalDateTime date) {
            this.description = description;
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getDate() {
            return date;
        }
    }
}
