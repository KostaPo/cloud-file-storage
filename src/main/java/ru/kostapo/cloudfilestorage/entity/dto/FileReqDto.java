package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileReqDto {
    String holder;
    String fullPath;
    MultipartFile file;
}
