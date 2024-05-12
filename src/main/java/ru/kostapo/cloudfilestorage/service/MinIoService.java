package ru.kostapo.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

@Service
@RequiredArgsConstructor
public class MinIoService implements S3Service {

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
