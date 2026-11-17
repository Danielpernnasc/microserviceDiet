package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.infra.Config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {
        SecurityConfig.class,
        SecurityConfigTest.TestBeans.class
})
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class
})
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvSource({
            "GET,/swagger-ui.html",
            "GET,/v3/api-docs",
            "GET,/swagger-resources",
            "POST,/auth/login",
            "POST,/auth/register"
    })
    @DisplayName("Swagger and auth endpoints are public")
    void publicEndpointsAreAccessible(String method, String path) throws Exception {
        mockMvc.perform(request(method, path))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Nutritionist endpoints require the NUTRITIONIST role")
    void nutritionistEndpointsRequireNutritionistRole() throws Exception {
        List<Endpoint> endpoints = List.of(
                new Endpoint("GET", "/Nutritionist/searchnutritionist/CRN-1"),
                new Endpoint("GET", "/Nutritionist/ListNutri"),
                new Endpoint("POST", "/Nutritionist"),
                new Endpoint("PUT", "/Nutritionist/UpdateNutri"),
                new Endpoint("PATCH", "/Nutritionist/PatchNutri"),
                new Endpoint("POST", "/Diet/athletediet/post"),
                new Endpoint("GET", "/Diet/athletediet/get/123"),
                new Endpoint("PATCH", "/Diet/athletediet/patch/123"),
                new Endpoint("PUT", "/Diet/athletediet/put/123"),
                new Endpoint("DELETE", "/Diet/athletediet/delete/123")
        );

        for (Endpoint endpoint : endpoints) {
            mockMvc.perform(request(endpoint.method(), endpoint.path()))
                    .andExpect(status().isForbidden());
            mockMvc.perform(request(endpoint.method(), endpoint.path())
                            .with(user("nutritionist").roles("NUTRITIONIST")))
                    .andExpect(status().isNotFound());
            mockMvc.perform(request(endpoint.method(), endpoint.path())
                            .with(user("athlete").roles("ATHLETE")))
                    .andExpect(status().isForbidden());
        }
    }

    private MockHttpServletRequestBuilder request(String method, String path) {
        return switch (method) {
            case "GET" -> get(path);
            case "POST" -> post(path);
            case "PUT" -> put(path);
            case "PATCH" -> patch(path);
            case "DELETE" -> delete(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    private record Endpoint(String method, String path) {
    }

    @TestConfiguration
    @EnableWebMvc
    @EnableWebSecurity
    static class TestBeans {
        @Bean
        UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }

        @Bean
        com.trainday.microserviceDiet.infra.service.JwtService jwtService() {
            return mock(com.trainday.microserviceDiet.infra.service.JwtService.class);
        }

        @Bean
        JwtAuthFilter jwtAuthFilter(com.trainday.microserviceDiet.infra.service.JwtService jwtService) {
            return new JwtAuthFilter(jwtService);
        }
    }
}
