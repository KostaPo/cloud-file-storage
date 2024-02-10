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
        Integer q = 123;
        log.info("info {}", q);
        model.addAttribute("greeting", "Hello KostaPo");
        return "index";
    }
}
  