package ru.kostapo.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;
import ru.kostapo.cloudfilestorage.service.UserService;

@Log4j2
@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegistration() {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        log.info("get request on register page");
        return "registration";
    }

    @PostMapping
    public String postRegistration(@RequestParam("username") String username, @RequestParam("password")String password) {
        UserReqDto user = UserReqDto.builder()
                .login(username)
                .password(password)
                .build();
        userService.save(user);
        log.info("register user with username {}, password {}", username, password);
        return "redirect:/login";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
