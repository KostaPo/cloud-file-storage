package ru.kostapo.cloudfilestorage.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReqDto {

    @NotNull(message = "Введите логин!")
    @NotEmpty(message = "Введите логин!")
    @Size(max = 10, message = "Логин больше 10 символов!")
    @Pattern(regexp = "[a-zA-Zа-яА-Я0-9]*", message = "Только буквы или цифры!")
    private String username;

    @NotEmpty(message = "Введите пароль!")
    @Size(min = 3, message = "Пароль меньше 3 символов!")
    private String password;
}
