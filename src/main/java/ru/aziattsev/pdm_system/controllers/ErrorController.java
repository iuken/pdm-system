package ru.aziattsev.pdm_system.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 0;

        if (statusObj != null) {
            statusCode = Integer.parseInt(statusObj.toString());
        }

        String message;
        if (statusCode == HttpStatus.FORBIDDEN.value()) {
            message = "Доступ запрещен";
        } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
            message = "Страница не найдена";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            message = "Внутренняя ошибка сервера";
        } else {
            message = "Произошла ошибка";
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("message", message);

        return "error/error"; // универсальная страница ошибок
    }
}
