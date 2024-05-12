package ru.kostapo.cloudfilestorage.entity;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {

    ROLE_GUEST,
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
