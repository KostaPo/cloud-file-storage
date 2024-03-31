package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping({"", "/"})
public class MainController {

    @GetMapping
    public String index(@AuthenticationPrincipal User user, Model model) {
        log.info("GET REQUEST on MAIN page");
        model.addAttribute("user", user);
        return "index";
    }
}
  