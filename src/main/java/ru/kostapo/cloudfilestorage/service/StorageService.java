package ru.kostapo.cloudfilestorage.service;

import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;

import java.util.List;

public interface StorageService {

    List<MinIoResObject> getAllObjectsByPath(String username, String path);

    void uploadFiles(List<MinIoReqObject> filesList, String dirPath);

    void uploadFolder(String username, String dirPath, String dirName);
}
