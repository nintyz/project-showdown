package com.projectshowdown.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    /**
     * Note: '*' matches zero or more characters, e.g., /books/* matches /books/20
     * '**' matches zero or more 'directories' in a path, e.g., /books/** matches
     * /books/1/reviews
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/error").permitAll() // the default error page
                        .requestMatchers(HttpMethod.GET, "/users", "/user/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").hasAuthority("admin")
                        .requestMatchers(HttpMethod.POST, "/addRandomData").permitAll()
                        .requestMatchers(HttpMethod.POST, "/chatbot/message").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/user/*").hasAuthority("admin")
                        .requestMatchers(HttpMethod.DELETE, "/user/*").hasAuthority("admin")
                        // note that Spring Security 6 secures all endpoints by default
                        .anyRequest().permitAll())
                // ensure that the application won’t create any session in our stateless REST
                // APIs
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // CSRF protection is needed only for browser based attacks
                .formLogin(form -> form.disable())
                .headers(header -> header.disable()); // disable the security headers, as we do not return HTML in our
        return http.build();
    }

    /**
     * @Bean annotation is used to declare a PasswordEncoder bean in the Spring
     *       application context.
     *       Any calls to encoder() will then be intercepted to return the bean
     *       instance.
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
}
