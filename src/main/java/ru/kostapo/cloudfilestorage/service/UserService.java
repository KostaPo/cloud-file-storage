package ru.kostapo.cloudfilestorage.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

public interface UserService extends UserDetailsService {

    void save(UserReqDto userDto);

}
