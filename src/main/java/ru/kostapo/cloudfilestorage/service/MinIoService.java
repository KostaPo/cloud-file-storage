package ru.kostapo.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoReqObject;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.exception.StorageException;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.repository.MinioRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        if (object.isItIsDir()) {
            log.info("RENAME FOLDER");
            List<Item> objectsForRename = minioRepository.getAllByPath(username, object.getFullPath() + object.getObjectName());
            for (Item i : objectsForRename) {
                MinIoResObject tmp = ObjectMapper.INSTANCE.itemToMinIoResObject(i);
                if (!isObjectDir(i)) {
                    replaceFolderName(username, tmp, object.getObjectName(), newName);
                }
            }
            deleteObject(username, object);
        } else {
            log.info("RENAME FILE");
            replaceFileName(username, object, newName);
        }
    }

    @Override
    public InputStreamResource downloadObject(String username, MinIoResObject object) {
        if (object.isItIsDir()) {
            log.info("DOWNLOAD FOLDER");
            return new InputStreamResource(getZipByteArrayByFolder(username, object));
        } else {
            log.info("DOWNLOAD FILE");
            return new InputStreamResource(
                    minioRepository.downloadFile(username, object.getFullPath() + object.getObjectName()));
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
        return !item.isDir() && item.objectName().endsWith("/");
    }

    private void replaceFileName(String username, MinIoResObject file, String newName) {
        String oldSource = file.getFullPath() + file.getObjectName();
        String newSource = file.getFullPath() + getChangeFileName(file.getObjectName(), newName);
        minioRepository.copyObject(username, oldSource, newSource);
        deleteObject(username, file);
    }

    public static String getChangeFileName(String oldName, String newName) {
        int lastDotIndex = oldName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            String fileExtension = oldName.substring(lastDotIndex);
            return newName + fileExtension;
        }
        return newName;
    }

    private ByteArrayInputStream getZipByteArrayByFolder (String username, MinIoResObject folderObject) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            List<Item> objects =
                    minioRepository.getAllByPath(username, folderObject.getFullPath() + folderObject.getObjectName());
            addToZip(username, zipOut, objects);
        } catch (IOException e) {
            log.error("Error creating zip archive", e);
            throw new StorageException("Can't download folder");
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private void addToZip(String username, ZipOutputStream zipOut, List<Item> objects) {
        for (Item i : objects) {
            if (!isObjectDir(i)) {
                MinIoResObject tmp = ObjectMapper.INSTANCE.itemToMinIoResObject(i);
                try (InputStream inputStream =
                             minioRepository.downloadFile(username, tmp.getFullPath() + tmp.getObjectName())) {
                    ZipEntry zipEntry = new ZipEntry(tmp.getFullPath() + tmp.getObjectName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, length);
                    }
                    zipOut.closeEntry();
                } catch (IOException e) {
                    log.error("Error reading file: " + tmp.getObjectName(), e);
                    throw new StorageException("Error downloading file: " + tmp.getObjectName());
                }
            }
        }
    }
}
