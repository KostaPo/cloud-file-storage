package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.service.FileServiceImpl;

import java.io.File;
import java.io.IOException;

@Log4j2
@Controller
@RequestMapping("/files")
public class FileController {

    @Value("${dropzone.max.file.size}")
    private Integer maxFileSize;

    private final FileServiceImpl fileService;

    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public String uploadFiles(@AuthenticationPrincipal User user,
                              @RequestParam("files") MultipartFile[] files,
                              Model model) {
        log.info("POST REQUEST on FILE controller by user [{}]", user.getUsername());
        for (MultipartFile multipartFile : files) {
            System.out.println(multipartFile.getOriginalFilename());
        }
        model.addAttribute("user", user);
        return "index";
    }









    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
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
