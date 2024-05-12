package ru.kostapo.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.io.File;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileController {

    private final MinIoService minIoService;

    @PostMapping
    public ResponseEntity<?> uploadFiles(HttpServletRequest request, @AuthenticationPrincipal User user,
                                         @RequestParam("data") MultipartFile[] files) {
        log.info("POST REQUEST on FILE controller by user [{}]", user.getUsername());
        for (MultipartFile multipartFile : files) {
            System.out.println(request.getContentLength());
            System.out.println("name: " + multipartFile.getOriginalFilename() + " size: " + multipartFile.getSize());
            //fileService.uploadFiles(user.getUsername(), multipartFile);
        }
        return ResponseEntity.ok("ok");
    }

    public void listFilesAndDirectories(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Directory: " + file.getName());
                    listFilesAndDirectories(file.getAbsolutePath());
                } else {
                    System.out.println("File: " + file.getName());
                }
            }
        }
    }
}
