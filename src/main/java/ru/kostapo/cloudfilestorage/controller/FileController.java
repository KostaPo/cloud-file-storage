package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.service.FileServiceImpl;

import java.io.IOException;

@Log4j2
@Controller
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileService;

    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, Model model) throws IOException {
        for (MultipartFile file : files) {
            fileService.uploadFiles(file);
        }
        log.info("POST REQUEST on FILE controller");
        return "index";
    }
}
