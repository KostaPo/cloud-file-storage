package ru.kostapo.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String getRegistration(Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        log.info("get request on register page");
        model.addAttribute("userDto", new UserReqDto());
        return "registration";
    }

    @PostMapping
    public String postRegistration(@ModelAttribute("userDto") @Valid UserReqDto user, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.info("BindingResult has errors");
            return "registration";
        }
        userService.save(user);
        log.info("register user with username {}, password {}",
                user.getUsername(),
                user.getPassword());
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
