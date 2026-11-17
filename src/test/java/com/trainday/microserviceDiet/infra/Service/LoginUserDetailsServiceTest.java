package com.trainday.microserviceDiet.infra.Service;

import com.trainday.microserviceDiet.Repository.LoginNutritionistRepository;
import com.trainday.microserviceDiet.Repository.NutritionistRepository;
import com.trainday.microserviceDiet.infra.service.LoginUserDetailsService;
import com.trainday.microserviceDiet.models.LoginNutri;
import com.trainday.microserviceDiet.models.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginUserDetailsServiceTest {
    @Mock
    NutritionistRepository nutritionistRepository;

    @Mock
    LoginNutritionistRepository loginRepository;

    @InjectMocks
    LoginUserDetailsService loginUserDetailsService;

    @BeforeEach
    void setup() {
        loginUserDetailsService =
                new LoginUserDetailsService(loginRepository);
    }

    @Test
    void shouldLoadByUserName(){
        LoginNutri login = new LoginNutri();
        login.setId("User1");
        login.setEmail("nutritionist@host.com.br");
        login.setCrn("CRN123456");
        login.setPassword("123456");
        login.setRole(Role.NUTRITIONIST);

        when(loginRepository.findByEmail("nutritionist@host.com.br"))
                .thenReturn(Optional.of(login));


        UserDetails userDetails =
                loginUserDetailsService.loadUserByUsername("nutritionist@host.com.br");


        assertEquals("nutritionist@host.com.br", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(loginRepository.findByEmail("nutritionist@host.com.br"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> loginUserDetailsService.loadUserByUsername("nutritionist@host.com.br")
        );
    }
}
