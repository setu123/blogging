package com.square.blogging.controller;

import com.square.blogging.model.Blog;
import com.square.blogging.service.BlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import java.security.Principal;

@Controller
@RequestMapping("blog")
public class BlogController {

    private static final Logger log = LoggerFactory.getLogger(BlogController.class);
    @Autowired
    private BlogService blogService;

    @GetMapping
    public String getBlogs(Model model) {
        model.addAttribute("blogs", new ReactiveDataDriverContextVariable(blogService.getBlogs()));
        return "blog";
    }

    @GetMapping("{id}")
    public String getBlog(@PathVariable long id, Model model) {
        model.addAttribute("blog", blogService.getBlog(id));
        return "blogView";
    }

    @GetMapping("create")
    public String getBlogCreationForm(Model model) {
        model.addAttribute("blog", new Blog());
        return "createBlog";
    }

    @PutMapping("{id}")
    public String publish(@PathVariable long id) {
        blogService.publishBlog(id);
        return "redirect:/blog";
    }

    @PostMapping
    public String create(@ModelAttribute Blog blog, @AuthenticationPrincipal Principal principal) {
        blogService.createBlog(blog, principal);
        return "redirect:/blog";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable long id) {
        blogService.deleteBlog(id);
        return "redirect:/blog";
    }
}
