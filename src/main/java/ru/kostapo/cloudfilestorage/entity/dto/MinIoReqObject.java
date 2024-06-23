package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MinIoReqObject {
    private String holder;
    private String fullPath;
    private MultipartFile file;
}
