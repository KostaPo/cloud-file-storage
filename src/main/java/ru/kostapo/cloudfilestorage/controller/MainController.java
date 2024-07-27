package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kostapo.cloudfilestorage.entity.dto.BreadcrumbsDto;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.BreadcrumbsMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping({"", "/"})
public class MainController {

    private final MinIoService minIoService;

    @GetMapping
    public String index(@AuthenticationPrincipal User user,
                        @RequestParam(required = false) String path,
                        Model model) {
        log.info("GET REQUEST on MAIN page with PATH {}", path);
        BreadcrumbsDto breadcrumbs = BreadcrumbsMapper.INSTANCE.mapToDto(path == null ? "" : path);
        List<MinIoResObject> items = minIoService.getAllObjectsByPath(user.getUsername(), path == null ? "" : path);
        model.addAttribute("user", user);
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("items", items);
        return "index";
    }
}
  