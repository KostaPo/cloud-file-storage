package ru.kostapo.cloudfilestorage.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReqDto {

    @NotNull(message = "Логин: введите логин!")
    @NotEmpty(message = "Логин: введите логин!")
    @Size(max = 10, message = "Логин: больше 10 символов!")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]*", message = "Логин: только буквы или цифры!")
    private String username;

    @NotEmpty(message = "Пароль: введите пароль!")
    @Size(min = 3, message = "Пароль: меньше 3 символов!")
    private String password;
}
