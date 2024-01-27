package ru.kostapo.cloudfilestorage;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
public class MainController {


    @RequestMapping({"", "/"})
    @ResponseBody
    public String index() {
        log.info("info");
        return "Hello";
    }
}
