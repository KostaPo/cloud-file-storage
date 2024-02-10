package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse;

@Log4j2
@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        log.info("get request on login page");
        if (error != null) {
            model.addAttribute("error", error);
            log.info("login page request with error: " + error);
        }
        if (logout != null) {
            model.addAttribute("logout", logout);
            log.info("login page request with logout: " + logout);
        }
        return "login";
    }


    @PostMapping
    public String postLogin() {
        log.info("post request on login page");
        return "login";
    }
}
