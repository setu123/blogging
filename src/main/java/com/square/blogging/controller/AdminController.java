package com.square.blogging.controller;

import com.square.blogging.exception.UserAlreadyExists;
import com.square.blogging.model.User;
import com.square.blogging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAdmins(Model model, Authentication authentication) {
        authentication.getAuthorities().forEach(auth -> System.out.println("authority: " + auth));
        model.addAttribute("admins", new ReactiveDataDriverContextVariable(userService.getAdmins()));
        return "admin";
    }

    @GetMapping("create")
    public String getAdminCreationForm(Model model) {
        model.addAttribute("admin", new User());
        return "createAdmin";
    }

    @PostMapping
    public Mono<String> create(@ModelAttribute User user, Model model) {
        try {
            userService.createAdminUser(user);
            return Mono.just("redirect:/admin");
        } catch (UserAlreadyExists userAlreadyExists) {
            return Mono.just("redirect:/create");
        }
    }
}
