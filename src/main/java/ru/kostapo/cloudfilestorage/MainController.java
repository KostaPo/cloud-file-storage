package ru.kostapo.cloudfilestorage;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
public class MainController {

    @RequestMapping({"", "/"})
    public String index(Model model) {
        Integer q = 123;
        log.info("info {}", q);
        model.addAttribute("greeting", "Welcome to our dynamic website!");
        return "index";
    }
}
