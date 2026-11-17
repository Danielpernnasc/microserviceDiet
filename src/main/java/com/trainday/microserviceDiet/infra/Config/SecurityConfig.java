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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
public class SecurityConfig {
    private static final String AUTH = "/auth/*";
    private static final String NUTRI_SEARCH = "/Nutritionist/searchnutritionist/*";
    private static final String NUTRI_POST = "/Nutritionist";
    private static final String NUTRI_LIST_ALL = "/Nutritionist/ListNutri";
    private static final String NUTRI_UPDATE = "/Nutritionist/UpdateNutri";
    private static final String NUTRI_PATCH = "/Nutritionist/PatchNutri";
    private static final String DIET_POST = "/Diet/athletediet/post";
    private static final String DIET_GET = "/Diet/athletediet/get/*";
    private static final String DIET_PATCH = "/Diet/athletediet/patch/*";
    private static final String DIET_PUT = "/Diet/athletediet/put/*";
    private static final String DIET_DELETE = "/Diet/athletediet/delete/*";
    private final JwtAuthFilter jwtAuthFilter;
    private static final String NUTRITIONIST = "NUTRITIONIST";


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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Authentication uses Bearer tokens in the Authorization header, not session cookies.
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
                        .requestMatchers(HttpMethod.GET, NUTRI_SEARCH).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.GET, NUTRI_LIST_ALL).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.POST, NUTRI_POST).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod. PUT, NUTRI_UPDATE).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.PATCH, NUTRI_PATCH).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.POST, DIET_POST).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.GET, DIET_GET).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.PATCH, DIET_PATCH).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.PUT, DIET_PUT).hasRole(NUTRITIONIST)
                        .requestMatchers(HttpMethod.DELETE, DIET_DELETE).hasRole(NUTRITIONIST)
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
