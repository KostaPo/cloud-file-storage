package ru.kostapo.cloudfilestorage.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileResDto {
    String filename;
    String size;
    String path;
    MultipartFile file;
}
