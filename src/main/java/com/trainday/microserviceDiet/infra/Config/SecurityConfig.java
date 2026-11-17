package com.trainday.microserviceDiet.infra.Config;

import com.trainday.microserviceDiet.infra.Security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    private static final String NUTRISearch = "/Nutritionist/searchnutritionist/*";
    private static final String NutriPost = "/Nutritionist";
    private static final String NutriListALL = "/Nutritionist/ListNutri";
    private static final String NutriUpdate = "/Nutritionist/UpdateNutri";
    private static final String Nutripatch= "/Nutritionist/PatchNutri";
    private static final String DietPost = "/Diet/athletediet/post";
    private static final String DietGet  = "/Diet/athletediet/get/*";
    private static final String DietPatch  = "/Diet/athletediet/patch/*";
    private static final String DietPut  = "/Diet/athletediet/put/*";
    private static final String DietDelete  = "/Diet/athletediet/delete/*";
    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
       return http
                .securityMatcher(swaggerRequestMatcher())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/error",
                                "/auth/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, NUTRISearch).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.GET, NutriListALL).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.POST, NutriPost).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod. PUT, NutriUpdate).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.PATCH, Nutripatch).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.POST, DietPost).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.GET, DietGet).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.PATCH, DietPatch).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.PUT, DietPut).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.DELETE, DietDelete).hasRole("NUTRITIONIST")
                        .requestMatchers(HttpMethod.POST, AUTH).permitAll()
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
        return config.getAuthenticationManager();
    }
}
