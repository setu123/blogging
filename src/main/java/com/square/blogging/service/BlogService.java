package com.square.blogging.service;

import com.square.blogging.model.Blog;
import com.square.blogging.model.User;
import com.square.blogging.repository.BlogRepository;
import com.square.blogging.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private UserRepository userRepository;

    public Optional<Blog> createBlog(Blog blog, Principal principal){
        return userRepository.findByUsername(principal.getName()).map(user -> {
            blog.setAuthor(user);
            blog.setStatus(Blog.Status.DRAFT);
            return blog;
        }).map(blg -> {
            return blogRepository.save(blg);
        });
    }

    public Flux<Blog> getBlogs(){
        return Flux.fromIterable(blogRepository.findAll());
    }

    public Mono<Blog> getBlog(long id){
        return Mono.justOrEmpty(blogRepository.findById(id));
    }

    public void deleteBlog(long id){
        blogRepository.deleteById(id);
    }

    public void publishBlog(long id){
        blogRepository.findById(id)
                .ifPresent(blog -> {
                    blog.setStatus(Blog.Status.PUBLISHED);
                    blogRepository.save(blog);
                });
    }
}
