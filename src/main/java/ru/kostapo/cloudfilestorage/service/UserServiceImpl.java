package ru.kostapo.cloudfilestorage.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kostapo.cloudfilestorage.entity.AppUser;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;
import ru.kostapo.cloudfilestorage.repository.UserRepository;

import java.util.List;

@Log4j2
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(UserReqDto userDto) {
        AppUser user = new AppUser();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findAppUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username = " + username + " not exist!"));
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        log.info("load UserDetails username:" + username + " role:" + user.getRole().name() + " in user service");
        return new User(user.getUsername(), user.getPassword(), roles);
    }
}
