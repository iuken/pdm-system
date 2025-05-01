package ru.aziattsev.pdm_system.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.aziattsev.pdm_system.entity.DocumentRequest;
import ru.aziattsev.pdm_system.entity.PdmUser;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;
import ru.aziattsev.pdm_system.services.DocumentService;

@Controller
public class AppController {
    @Autowired

    DocumentService documentService;
    @Autowired
    PdmUserRepository pdmUserRepository;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }


    @PostMapping("/update")
    public void updateDocumentInfo2(@Valid @RequestBody DocumentRequest request){
        documentService.updateFromCad(request);

    }
//    @PostMapping("/")
//    public void updateDocumentInfo(@Valid @RequestBody DocumentRequest request){
//        documentService.updateFromCad(request);
//
//    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            PdmUser user = pdmUserRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            model.addAttribute("user", user);
        }
        return "home";
    }
}
