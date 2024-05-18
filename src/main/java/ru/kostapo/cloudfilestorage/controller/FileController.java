package ru.kostapo.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.FileReqDto;
import ru.kostapo.cloudfilestorage.mapper.FileMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.io.File;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileController {

    private final MinIoService minIoService;

    @PostMapping
    public ResponseEntity<?> uploadFiles(@AuthenticationPrincipal User user,
                                         @RequestParam("data") MultipartFile[] files) {
        log.info("POST request on UPLOAD by user [{}]", user.getUsername());
        List<FileReqDto> dtoFileList = FileMapper.INSTANCE.multipartFilesToListDto(user.getUsername(), files);
        for (FileReqDto dto : dtoFileList) {

            System.out.println("name: " + dto.getFullPath() + " holder: " + dto.getHolder());
        }
        return ResponseEntity.ok("ok");
    }

}
