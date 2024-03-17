package ru.kostapo.cloudfilestorage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

@Service
public class FileServiceImpl implements FileService {

    private final MinioRepository minioRepository;

    public FileServiceImpl(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }

    @Override
    public void uploadFiles(MultipartFile multipartFile) {
        minioRepository.uploadFile(multipartFile);
    }
}
