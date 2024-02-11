package ru.kostapo.cloudfilestorage.entity;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {

    GUEST, USER, ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
