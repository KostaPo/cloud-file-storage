package ru.kostapo.cloudfilestorage.entity.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileReqDto {

    @Pattern(regexp = "^[a-zA-Z0-9()а-яА-Я]+$", message = "Имя может содержать только буквы или цифры!")
    String filename;
    String size;
    String path;
    MultipartFile file;
}
