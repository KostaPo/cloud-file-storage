package ru.kostapo.cloudfilestorage.service;

import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.FileReqDto;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

public interface FileService {

    void uploadFiles(MultipartFile file);

}
