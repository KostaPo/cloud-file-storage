package ru.kostapo.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinIoService implements StorageService {

    private final MinioRepository minioRepository;

    @Override
    public List<MinIoResObject> getAllObjectsByFolder(String username, String folderPath) {
        List<Item> items = minioRepository.getAllByFolder(username, folderPath);
        return ObjectMapper.INSTANCE.mapItemsToMinIoResObjects(items);
    }

    @Override
    public List<MinIoResObject> getAllObjectsBySearchQuery(String username, String query) {
        List<MinIoResObject> result = new ArrayList<>();
        List<Item> items = minioRepository.getAllByUser(username);
        for (Item item : items) {
            String objectName = getObjectName(item.objectName());
            if (objectName.toLowerCase().contains(query.toLowerCase())) {
                result.add(ObjectMapper.INSTANCE.itemToMinIoResObject(item));
            }
        }
        return result;
    }

    @Override
    public void uploadFiles(List<MinIoReqObject> files, String path) {
        for (MinIoReqObject dto : files) {
            minioRepository.uploadFile(dto.getHolder(), path, dto.getFile());
        }
    }

    @Override
    public void uploadFolder(String username, String path, String name) {
        minioRepository.uploadFolder(username, path, name);
    }

    @Override
    public void deleteObject(String username, MinIoResObject object) {
        if (object.isItIsDir()) {
            minioRepository.removeFolder(username, object.getFullPath() + object.getObjectName());
        } else {
            minioRepository.removeFile(username, object.getFullPath() + object.getObjectName());
        }
    }

    @Override
    public void renameObject(String username, MinIoResObject object, String newName) {
        String oldSource = object.getFullPath() + object.getObjectName();
        String newSource = object.getFullPath() + newName;
        minioRepository.copyObject(username, oldSource, newSource);
        deleteObject(username, object);
    }

    private String getObjectName(String objectPath) {
        String[] parts = objectPath.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                return parts[i];
            }
        }
        return "";
    }
}
