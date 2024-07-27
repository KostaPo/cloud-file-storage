package ru.kostapo.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinIoService implements StorageService {

    private final MinioRepository minioRepository;

    @Override
    public List<MinIoResObject> getAllObjectsByPath(String username, String path) {
        List<Item> items = minioRepository.getAllByPath(username, path);
        return ObjectMapper.INSTANCE.mapItemsToMinIoResObjects(items);
    }

    @Override
    public void uploadFiles(List<MinIoReqObject> files, String dirPath) {
        for (MinIoReqObject dto : files) {
            System.out.println("file: " + dto.getFullPath() + " holder: " + dto.getHolder());
            minioRepository.uploadFile(dto.getHolder(), dirPath, dto.getFile());
        }
    }

    @Override
    public void uploadFolder(String username, String dirPath, String dirName) {
        minioRepository.uploadFolder(username, dirPath, dirName);
    }

    @Override
    public void removeFile(String username, String path) {
        minioRepository.removeFile(username, path);
    }

    @Override
    public void removeFolder(String username, String path) {
        minioRepository.removeFolder(username, path);
    }
}
