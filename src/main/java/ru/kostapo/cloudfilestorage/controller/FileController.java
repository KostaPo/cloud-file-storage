package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.BreadcrumbsDto;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.BreadcrumbsMapper;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final MinIoService minIoService;

    @PostMapping("/upload")
    public String uploadFiles(@AuthenticationPrincipal User user,
                              @RequestParam(value = "path", required = false) String path,
                              @RequestParam("data") MultipartFile[] files,
                              Model model) {

        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        List<MinIoReqObject> dtoFiles = ObjectMapper.INSTANCE.multipartFilesToMinIoObjectList(user.getUsername(), files);
        minIoService.uploadFiles(dtoFiles, path);
        log.info("upload [{}] files to [{}]", files.length, String.join("/", user.getUsername(), path));
        List<MinIoResObject> items = minIoService.getAllObjectsByPath(user.getUsername(), path);
        BreadcrumbsDto breadcrumbs = BreadcrumbsMapper.INSTANCE.mapToDto(path == null ? "" : path);
        model.addAttribute("items", items);
        model.addAttribute("breadcrumbs", breadcrumbs);
        return "fragments/common :: itemsList";
    }

    @DeleteMapping(value = "/remove")
    public String removeFile(@AuthenticationPrincipal User user,
                             @RequestParam(value = "path", required = false) String path,
                             @RequestParam(value = "objectName", required = false) String objectName) {
        minIoService.removeFile(user.getUsername(), path + objectName);
        log.info("remove file [{}] on path [{}]", objectName, path);
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}

