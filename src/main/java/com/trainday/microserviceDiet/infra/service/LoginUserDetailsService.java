package com.trainday.microserviceDiet.infra.service;

import com.trainday.microserviceDiet.Repository.LoginNutricionistRepository;
import com.trainday.microserviceDiet.models.LoginNutri;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginUserDetailsService implements UserDetailsService {
    private final LoginNutricionistRepository repository;
    private final static String Professional_not_found = "Professional not found";

    public LoginUserDetailsService(LoginNutricionistRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Optional<LoginNutri> user = repository.findByEmail(login);

        if (user.isEmpty()) {
            user = repository.findByCrn(login);
        }

        LoginNutri loginNutri = user.orElseThrow(
                () -> new UsernameNotFoundException("Professional not found"));


        if (loginNutri.getRole() == null) {
            throw new UsernameNotFoundException("Professional has no role assigned");
        }

        return User.builder()
                .username(loginNutri.getEmail())
                .password(loginNutri.getPassword())
                .authorities("ROLE_" + loginNutri.getRole().name())
                .build();
    }
}
