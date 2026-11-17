package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.infra.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtAuthFilter  extends OncePerRequestFilter {
    private static final Logger log = Logger.getLogger(JwtAuthFilter.class.getName());

    private final JwtService jwtService;


    public JwtAuthFilter(
            JwtService jwtService) {
        this.jwtService = jwtService;
    }

   @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String path = request.getServletPath();
        return path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.equals("/trainTemplate/templates")
                || path.equals("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("=== JwtAuthFilter ===");
        String authHeader = request.getHeader("Authorization");
        System.out.println("Path: " + request.getServletPath());
        System.out.println("Authorization: " + request.getHeader("Authorization"));
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        boolean validToken = jwtService.isTokenValid(token);
        System.out.println("Token válido: " + validToken);
        if (validToken) {
            String login = jwtService.extractSubject(token);

            String role = jwtService.extractRole(token);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    login, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth);

        }
        filterChain.doFilter(request, response);

    }
}
