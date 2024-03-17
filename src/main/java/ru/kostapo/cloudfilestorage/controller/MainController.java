package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping({"", "/"})
public class MainController {

    @GetMapping
    public String index(Model model) {
        log.info("GET REQUEST on MAIN page");
        model.addAttribute("greeting", "Hello KostaPo");
        return "index";
    }
}
  