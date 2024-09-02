package ru.kostapo.cloudfilestorage.service;

import io.lettuce.core.ScriptOutputType;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MinIoService implements StorageService {

    private final MinioRepository minioRepository;

    @Override
    public List<MinIoResObject> getAllObjectsByFolder(String username, String folderPath) {
        log.info("GET ALL BY FOLDER");
        List<Item> items = minioRepository.getAllByFolder(username, folderPath);
        return ObjectMapper.INSTANCE.mapItemsToMinIoResObjects(items);
    }

    @Override
    public List<MinIoResObject> getAllObjectsBySearchQuery(String username, String query) {
        log.info("FIND ALL BY SEARCH QUERY");
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
        log.info("UPLOAD FILES");
        for (MinIoReqObject dto : files) {
            minioRepository.uploadFile(dto.getHolder(), path, dto.getFile());
        }
    }

    @Override
    public void uploadFolder(String username, String path, String name) {
        log.info("UPLOAD FOLDER");
        minioRepository.uploadFolder(username, path, name);
    }

    @Override
    public void deleteObject(String username, MinIoResObject object) {
        if (object.isItIsDir()) {
            log.info("DELETE FOLDER");
            minioRepository.removeFolder(username, object.getFullPath() + object.getObjectName());
        } else {
            log.info("DELETE FILE");
            minioRepository.removeFile(username, object.getFullPath() + object.getObjectName());
        }
    }

    @Override
    public void renameObject(String username, MinIoResObject object, String newName) {
        System.out.println("NEW NAME: "  + newName);
        if (object.isItIsDir()) {
            log.info("RENAME FOLDER");
            List<Item> objectsForRename = minioRepository.getAllByPath(username, object.getFullPath() + object.getObjectName());
            for(Item i : objectsForRename) {
                MinIoResObject tmp = ObjectMapper.INSTANCE.itemToMinIoResObject(i);
                if(!isObjectDir(i)) {
                    replaceFolderName(username, tmp, object.getObjectName(), newName);
                }
            }
            deleteObject(username, object);
        } else {
            log.info("RENAME FILE");
            replaceFileName(username, object, newName);
        }
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

    private boolean isObjectDir(Item item) {
        System.out.println("check: " + item.isDir() + " + " + item.objectName());
        return !item.isDir() && item.objectName().endsWith("/");
    }

    private void replaceFileName(String username, MinIoResObject file, String newName) {
        String oldSource = file.getFullPath() + file.getObjectName();
        String newSource = file.getFullPath() + newName;
        minioRepository.copyObject(username, oldSource, newSource);
        deleteObject(username, file);
    }

    private void replaceFolderName(String username, MinIoResObject file, String oldName, String newName) {
        String oldPath = file.getFullPath() + file.getObjectName();
        String newPath = oldPath.replace(oldName, newName);
        minioRepository.copyObject(username, oldPath, newPath);
        deleteObject(username, file);
    }
}
