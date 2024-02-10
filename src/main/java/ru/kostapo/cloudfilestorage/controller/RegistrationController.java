package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @GetMapping
    public String getRegistration() {
        log.info("get request on register page");
        return "registration";
    }

    @PostMapping
    public String postRegistration() {
        log.info("post request on register page");
        return "registration";
    }
}
