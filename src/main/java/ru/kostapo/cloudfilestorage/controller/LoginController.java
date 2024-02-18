package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

@Log4j2
@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String getLogin(@RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "error", required = false) String error,
                           Model model) {
        if (isAuthenticated()) {
            return "redirect:/";
        }
        if (logout != null) {
            model.addAttribute("logout", logout);
            log.info("login page request with logout");
        }
        if (error != null) {
            model.addAttribute("error", error);
            log.info("login page request with error");
        }
        log.debug("get request on login page");
        model.addAttribute("userDto", new UserReqDto());
        return "login";
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
