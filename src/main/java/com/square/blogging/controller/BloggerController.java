package com.square.blogging.controller;

import com.square.blogging.exception.UserAlreadyExists;
import com.square.blogging.model.User;
import com.square.blogging.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/blogger")
public class BloggerController {

    private static final Logger log = LoggerFactory.getLogger(BloggerController.class);
    @Autowired
    private UserService userService;

    @GetMapping
    public String getBloggers(Model model){
        model.addAttribute("bloggers", new ReactiveDataDriverContextVariable(userService.getBloggers()));
        return "blogger";
    }

    @GetMapping("{id}")
    public String getBloggerById(@PathVariable long id, Model model){
        model.addAttribute("blogger", userService.findById(id));
        return "updateBlogger";
    }

    @GetMapping("register")
    public String registerBlogger(Model model){
        model.addAttribute("blogger", new User());
        return "register";
    }

    @PostMapping("register")
    public String create(@ModelAttribute User user, Model model){
        try {
            userService.createBlogger(user);
        } catch (UserAlreadyExists userAlreadyExists) {
            model.addAttribute("blogger", user);
            model.addAttribute("userExists", true);
            return "register";
        }
        return "thank";
    }

    @PutMapping("{id}")
    public String updateStatus(@PathVariable long id, @ModelAttribute User user){
        userService.updateUserStatus(id, user.getStatus());
        return "redirect:/blogger";
    }
}
