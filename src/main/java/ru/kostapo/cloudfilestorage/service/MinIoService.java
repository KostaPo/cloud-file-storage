package ru.kostapo.cloudfilestorage.service;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioRepository minioRepository;

    @Override
    public void getAll(String username) {
        minioRepository.getAll(username);
    }

    @Override
    public void uploadFiles(String username, MultipartFile multipartFile) {
        minioRepository.uploadFile(username, multipartFile);
    }
}
