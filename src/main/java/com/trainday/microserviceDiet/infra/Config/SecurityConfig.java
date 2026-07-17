package com.trainday.microserviceDiet.infra.Config;

import com.trainday.microserviceDiet.infra.Security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
public class SecurityConfig {
    private static final String AUTH = "/auth/*";
    private static final String NUTRISearch = "/Nutricionist/searchnutricionist/*";
    private static final String NutriPost = "/Nutricionist";
    private static final String NutriListALL = "/Nutricionist/ListNutri";
    private static final String NutriUpdate = "/Nutricionist/UpdateNutri";
    private static final String Nutripatch= "/Nutricionist/PatchNutri";
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/auth/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, NUTRISearch).hasRole("NUTRICIONIST")
                        .requestMatchers(HttpMethod.GET, NutriListALL).hasRole("NUTRICIONIST")
                        .requestMatchers(HttpMethod.POST, NutriPost).hasRole("NUTRICIONIST")
                        .requestMatchers(HttpMethod. PUT, NutriUpdate).hasRole("NUTRICIONIST")
                        .requestMatchers(HttpMethod.PATCH, Nutripatch).hasRole("NUTRICIONIST")
                        .requestMatchers(HttpMethod.POST, AUTH).hasRole("NUTRICIONIST")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private OrRequestMatcher swaggerRequestMatcher() {
        return new OrRequestMatcher(
                regexMatcher("^/v3/api-docs(/.*)?$"),
                regexMatcher("^/swagger-ui(/.*)?$"),
                regexMatcher("^/swagger-ui\\.html$"),
                regexMatcher("^/swagger-resources(/.*)?$"),
                regexMatcher("^/webjars(/.*)?$"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return (AuthenticationManager) config.getAuthenticationManager();
    }
}
