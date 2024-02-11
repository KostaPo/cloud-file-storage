package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserReqDto {
    private String login;
    private String password;
}
