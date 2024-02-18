package ru.kostapo.cloudfilestorage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="users",
        uniqueConstraints = @UniqueConstraint(columnNames = "username"),
        indexes = {@Index(name = "login_idx", columnList = "username")})
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable=false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    AppUserRole role = AppUserRole.GUEST;
}
