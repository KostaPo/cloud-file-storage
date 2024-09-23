package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.BreadcrumbsDto;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.exception.valid.ValidName;
import ru.kostapo.cloudfilestorage.mapper.BreadcrumbsMapper;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/object")
@Validated
public class ObjectController {

    private final MinIoService minIoService;

    @PostMapping("/upload/file")
    public String uploadFile(@AuthenticationPrincipal User user,
                             @RequestParam(value = "path", required = false) String path,
                             @RequestParam("data") MultipartFile[] files,
                             Model model) {

        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        List<MinIoReqObject> dtoFiles = ObjectMapper.INSTANCE.multipartFilesToMinIoObjectList(user.getUsername(), files);
        minIoService.uploadFiles(dtoFiles, path);
        log.info("upload [{}] files to [{}]", files.length, String.join("/", user.getUsername(), path));
        List<MinIoResObject> items = minIoService.getAllObjectsByFolder(user.getUsername(), path);
        BreadcrumbsDto breadcrumbs = BreadcrumbsMapper.INSTANCE.mapToDto(path == null ? "" : path);
        model.addAttribute("items", items);
        model.addAttribute("breadcrumbs", breadcrumbs);
        return "fragments/common :: itemsList";
    }

    @PostMapping("/upload/folder")
    public String uploadFolder(@AuthenticationPrincipal User user,
                               @RequestParam String path,
                               @RequestParam @ValidName String folderName) {
        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        minIoService.uploadFolder(user.getUsername(), path, folderName);
        log.info("create folder on path [{}] with name [{}]",
                String.join("/", user.getUsername(), path), folderName);
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }


    @DeleteMapping(value = "/delete")
    public String deleteObject(@AuthenticationPrincipal User user,
                               @RequestParam String path,
                               @ModelAttribute MinIoResObject object) {
        log.info("delete object [{}] by path [{}]", object.getObjectName(), object.getFullPath());
        minIoService.deleteObject(user.getUsername(), object);
        return "redirect:/?path=" + URLEncoder.encode(object.getFullPath(), StandardCharsets.UTF_8);
    }

    @PatchMapping(value = "/rename")
    public String renameObject(@AuthenticationPrincipal User user,
                               @RequestParam String path,
                               @RequestParam @ValidName String newName,
                               @ModelAttribute MinIoResObject object) {
        log.info("rename object [{}] on path [{}] with new name[{}]",
                object.getObjectName(), object.getFullPath(), newName);
        minIoService.renameObject(user.getUsername(), object, newName);
        return "redirect:/?path=" + URLEncoder.encode(object.getFullPath(), StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<?> downloadObject(@AuthenticationPrincipal User user,
                                            @RequestParam String path,
                                            @ModelAttribute MinIoResObject object) {
        InputStreamResource objectData = minIoService.downloadObject(user.getUsername(), object);
        String objectName = object.isItIsDir()
                ? String.format("%s.zip", object.getObjectName())
                : object.getObjectName();
        log.info("download object [{}]", objectName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"",
                        URLEncoder.encode(objectName, StandardCharsets.UTF_8)))
                .body(objectData);
    }
}
