package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileController {

    private final MinIoService minIoService;

    @PostMapping
    public String uploadFiles(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "targetDir", required = false) String targetDir,
                                         @RequestParam("data") MultipartFile[] files,
                                         Model model) {

        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        List<MinIoReqObject> dtoFiles = ObjectMapper.INSTANCE.multipartFilesToMinIoObjectList(user.getUsername(), files);
        minIoService.uploadFiles(dtoFiles, targetDir);
        List<MinIoResObject> items = minIoService.getAllObjectsByPath(user.getUsername(), targetDir);
        model.addAttribute("items", items);
        return "fragments/common :: itemsList";
    }


    @GetMapping
    public ResponseEntity<?> uploadFolder(@AuthenticationPrincipal User user,
                                          @RequestParam(value = "path", required = false) String path,
                                          @RequestParam(value = "dirName", required = false) String dirName) {
        log.info("GET request on UPLOAD folder");
        minIoService.uploadFolder(user.getUsername(), path, dirName);
        return ResponseEntity.ok("ok");
    }

}

