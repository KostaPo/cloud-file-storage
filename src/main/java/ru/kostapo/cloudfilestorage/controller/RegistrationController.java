package ru.kostapo.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;
import ru.kostapo.cloudfilestorage.exception.NonUniqConstraintException;
import ru.kostapo.cloudfilestorage.exception.NonValidConstraintException;
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
        log.info("GET REQUEST on REGISTRATION page");
        if (isAuthenticated()) {
            log.info("REGISTRATION REQUEST redirect:/");
            return "redirect:/";
        }
        model.addAttribute("userDto", new UserReqDto());
        return "registration";
    }

    @PostMapping
    public String postRegistration(@Valid @ModelAttribute("userDto") UserReqDto user, BindingResult bindingResult) {
        log.info("POST REQUEST on REGISTRATION page with dto: " + user);
        if (bindingResult.hasErrors()) {
            throw new NonValidConstraintException(bindingResult);
        }
        try {
            userService.save(user);
            log.info("REGISTER NEW USER with username [{}]", user.getUsername());
            return "redirect:/login";
        } catch (DataIntegrityViolationException e) {
            throw new NonUniqConstraintException(bindingResult);
        }
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
