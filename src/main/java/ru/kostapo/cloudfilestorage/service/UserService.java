package ru.kostapo.cloudfilestorage.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;
import ru.kostapo.cloudfilestorage.entity.dto.UserResDto;

public interface UserService extends UserDetailsService {
    void save(UserReqDto userDto);
}
