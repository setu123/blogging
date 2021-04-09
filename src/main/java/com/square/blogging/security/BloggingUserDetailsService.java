package com.square.blogging.security;

import com.square.blogging.model.Role;
import com.square.blogging.model.User;
import com.square.blogging.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userDetailsService")
@Transactional
//@Profile("custom")
public class BloggingUserDetailsService implements ReactiveUserDetailsService{

    private static final Logger log = LoggerFactory.getLogger(BloggingUserDetailsService.class);
    @Autowired
    private UserRepository userRepository;

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        //for log purpose
        roles.stream()
                .map(role -> role.getName().toString())
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                .forEach(authority -> log.info("authority: " + authority));

        return roles.stream()
                .map(role -> role.getName().toString())
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                .collect(Collectors.toList());
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("Came to load user, username:: " + username);

        try {
            return Mono.justOrEmpty(userRepository.findByUsername(username)
                    .map(user -> {
                        return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getStatus().equals(User.Status.APPROVED),
                        true,
                        true,
                        true,
                        getAuthorities(user.getRoles())
                        ); })
                    .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username)));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}

