package ru.kostapo.cloudfilestorage.service;

import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;

import java.util.List;

public interface StorageService {

    List<MinIoResObject> getAllObjectsByFolder(String username, String folderPath);

    List<MinIoResObject> getAllObjectsBySearchQuery(String username, String query);

    void uploadFiles(List<MinIoReqObject> filesList, String path);

    void uploadFolder(String username, String path, String name);

    void deleteObject(String username, MinIoResObject object);

    void renameObject(String username, MinIoResObject object, String newName);
}
