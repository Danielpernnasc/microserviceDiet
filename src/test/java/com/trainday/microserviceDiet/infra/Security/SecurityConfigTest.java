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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Autowired
    private com.trainday.microserviceDiet.infra.service.JwtService jwtService;

    @Test
    @DisplayName("API authenticates Bearer tokens without creating or retaining a session")
    void bearerAuthenticationIsStateless() throws Exception {
        String token = "stateless-test-token";
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractSubject(token)).thenReturn("nutritionist");
        when(jwtService.extractRole(token)).thenReturn("NUTRITIONIST");

        mockMvc.perform(post("/Nutritionist")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertNull(result.getRequest().getSession(false)));

        mockMvc.perform(get("/Nutritionist/ListNutri"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertNull(result.getRequest().getSession(false)));
    }

    @Test
    @DisplayName("API ignores authentication stored in an HTTP session")
    void sessionAuthenticationIsIgnored() throws Exception {
        MockHttpSession session = new MockHttpSession();
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "nutritionist", null, List.of(new SimpleGrantedAuthority("ROLE_NUTRITIONIST")));
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(authentication));

        mockMvc.perform(get("/Nutritionist/ListNutri").session(session))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            "GET,/swagger-ui.html",
            "GET,/swagger-ui/index.html",
            "GET,/v3/api-docs",
            "GET,/v3/api-docs/swagger-config",
            "GET,/swagger-resources",
            "GET,/webjars/swagger-ui/index.html",
            "POST,/auth/login",
            "POST,/auth/register"
    })
    @DisplayName("Swagger and auth endpoints are public")
    void publicEndpointsAreAccessible(String method, String path) throws Exception {
        mockMvc.perform(request(method, path))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "POST,/v3/api-docs",
            "PUT,/swagger-ui/index.html",
            "PATCH,/swagger-resources",
            "DELETE,/webjars/swagger-ui/index.html"
    })
    @DisplayName("Swagger endpoints require CSRF tokens for unsafe methods")
    void swaggerEndpointsRequireCsrfTokens(String method, String path) throws Exception {
        mockMvc.perform(request(method, path))
                .andExpect(status().isForbidden());
        mockMvc.perform(request(method, path).with(csrf()))
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
