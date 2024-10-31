package com.projectshowdown.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import com.projectshowdown.util.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projectshowdown.service.JwtRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsService userSvc) {
        this.userDetailsService = userSvc;
    }

    /**
     * Exposes a bean of DaoAuthenticationProvider, a type of AuthenticationProvider
     * Attaches the user details and the password encoder
     * 
     * @return
     */

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

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
                        .requestMatchers("/login").permitAll()

                        // users CRUD
                        .requestMatchers(HttpMethod.GET, "/users", "/user/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").hasAuthority("admin")
                        .requestMatchers(HttpMethod.POST, "/addRandomData").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/user/*").hasAuthority("admin")
                        .requestMatchers(HttpMethod.DELETE, "/user/*").hasAuthority("admin")

                        // tournaments CRUD
                        .requestMatchers(HttpMethod.GET, "/tournaments", "/tournaments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tournaments").hasAnyAuthority("admin","organizer")
                        .requestMatchers(HttpMethod.PUT, "/tournaments/**").hasAnyAuthority("admin","organizer")
                        .requestMatchers(HttpMethod.PUT, "/tournaments/*/register/*").hasAuthority("player")
                        .requestMatchers(HttpMethod.PUT, "/tournaments/*/cancelRegistration/*").hasAuthority("player")

                        // matches CRUD
                        .requestMatchers(HttpMethod.PUT, "/match/*").hasAnyAuthority("admin", "organizer")

                        // chat bot
                        .requestMatchers(HttpMethod.POST, "/chatbot/message").permitAll()
                        .anyRequest().permitAll())

                // ensure that the application won’t create any session in our stateless REST
                // APIs
                .cors( cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // CSRF protection is needed only for browser based attacks
                .formLogin(form -> form.disable())
                .oauth2Login((oauth2) -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .headers(header -> header.disable()) // disable the security headers, as we do not return HTML in our
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
