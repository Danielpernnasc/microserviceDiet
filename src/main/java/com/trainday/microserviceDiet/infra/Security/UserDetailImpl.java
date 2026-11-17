package com.trainday.microserviceDiet.infra.Security;

import com.trainday.microserviceDiet.models.LoginNutri;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class UserDetailImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LoginNutri login;

    public UserDetailImpl(LoginNutri login) {
        this.login = login;
    }

    @Override
    public String getUsername() {
        return login.getEmail();
    }

    @Override
    public String getPassword() {
        return login.getPassword();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
