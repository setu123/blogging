package com.square.blogging.security;

import com.square.blogging.model.Role;
import com.square.blogging.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig{

    private static final Logger log = LoggerFactory.getLogger(BloggingUserDetailsService.class);
    //@Autowired
    //private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                    .authorizeExchange()
                    .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .pathMatchers("/login", "/blogger/register", "/*.css", "/*.js", "/h2-console", "/h2-console/**").permitAll()
                    .pathMatchers("/**").authenticated()
                    .pathMatchers("/h2-console/**").permitAll()
                    .pathMatchers("/admin").hasRole("ADMIN")
                .and().formLogin()
                    .loginPage("/login")
                    //.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/"))
                .and().logout()
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .build();
    }

    /**
    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        authenticationManager.setPasswordEncoder(passwordEncoder());

        return authenticationManager;
    }
    */

    private Mono<com.square.blogging.model.User> blogger(Authentication authentication){
        String username = authentication.getName();
        System.out.println("username: " + username);
        return Mono.justOrEmpty(userRepository.findByUsername(username));
    }

    public Mono<UserDetails> findByUsername(String username) {
        //log.info("Came to load user, username: " + username);

        try {
            return Mono.justOrEmpty(userRepository.findByUsername(username)
                    .map(user -> {
                        return new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                user.getStatus().equals(com.square.blogging.model.User.Status.APPROVED),
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

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
         return roles.stream()
                .map(role -> role.getName().toString())
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                .collect(Collectors.toList());
    }


    ReactiveAuthenticationManager customersAuthenticationManager() {
        log.info("This is customersAuthenticationManager");
        return authentication -> blogger(authentication)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usernot found")))
                .map(b -> new UsernamePasswordAuthenticationToken(b.getUsername(), b.getPassword(), getAuthorities(b.getRoles())));
    }

    ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver() {
        log.info("This is resolver");
        return exchange -> {
            return Mono.just(customersAuthenticationManager());
        };
    }


    /**
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }
     */


    //@Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        //authProvider.setPasswordEncoder(encoder());
        //authProvider.setPostAuthenticationChecks(differentLocationChecker);
        return authProvider;
    }
    */
}