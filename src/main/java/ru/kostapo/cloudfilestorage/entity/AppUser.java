package ru.kostapo.cloudfilestorage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="app_users",
        uniqueConstraints = @UniqueConstraint(columnNames = "login"),
        indexes = {@Index(name = "login_idx", columnList = "login")})
public class AppUser {

    @Id
    private Long id;

    @NotEmpty(message = "Введите логин!")
    @Size(min = 3, max = 32, message = "Логин должен быть от 3 до 32 символов!")
    @Column(updatable=false)
    private String login;

    @NotEmpty(message = "Введите пароль!")
    @Size(min = 3, message = "Пароль должен быть от 3 символов!")
    private String password;

    @Enumerated(EnumType.STRING)
    AppUserRole role = AppUserRole.GUEST;
}
