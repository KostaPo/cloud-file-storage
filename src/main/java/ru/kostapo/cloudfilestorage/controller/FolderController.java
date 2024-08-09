package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final MinIoService minIoService;

    @PostMapping("/upload")
    public String uploadFolder(@AuthenticationPrincipal User user,
                               @RequestParam(value = "path", required = false) String path,
                               @RequestParam(value = "folderName") String folderName) {
        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        minIoService.uploadFolder(user.getUsername(), path, folderName);
        log.info("create folder on path [{}] with name [{}]",
                String.join("/", user.getUsername(), path), folderName);
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }


    @DeleteMapping(value = "/remove")
    public String removeFolder(@AuthenticationPrincipal User user,
                               @RequestParam(value = "path", required = false) String path,
                               @RequestParam(value = "objectName", required = false) String objectName) {
        log.info("remove folder [{}] on path [{}]", objectName, path);
        minIoService.removeFolder(user.getUsername(), path + objectName);
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
