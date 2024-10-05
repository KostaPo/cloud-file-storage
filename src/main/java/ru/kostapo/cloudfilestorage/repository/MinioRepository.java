package ru.kostapo.cloudfilestorage.repository;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import io.minio.messages.Source;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.exception.FileNotFoundException;
import ru.kostapo.cloudfilestorage.exception.StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Log4j2
@RequiredArgsConstructor
@Repository
public class MinioRepository {

    @Value("${minio.client.bucket-name}")
    private String bucketName;
    private final Path tempDirectory = Path.of(System.getProperty("java.io.tmpdir"));

    private final MinioClient minioClient;

    @PostConstruct
    public void createAppRootBucket() {
        try {
            if (!isBucketExists(bucketName)) {
                log.info("create MinIO bucket with name [{}]", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info ("bucket created with temp directory [{}]", tempDirectory);
            }
            log.info("MinIO bucket [{}] already created", bucketName);
            log.info ("temp directory [{}]", tempDirectory);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new RuntimeException("Storage service can't create root bucket");
        }
    }
    
    public boolean isBucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new RuntimeException("Can't check root bucket exists");
        }
    }

    public void uploadFile(String username, String path, MultipartFile file) {
        log.info("user [{}] try to upload file...", username);
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(stream, file.getSize(), -1)
                    .bucket(bucketName)
                    .object(username + "/" + path + file.getOriginalFilename())
                    .build());
            log.info("user [{}] upload file '{}' ", username, file.getOriginalFilename());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't upload file");
        }
    }

    public void uploadFolder(String username, String path, String name) {
        log.info("user [{}] try to upload folder...", username);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(username + "/" + path + name + "/")
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
            log.info("user [{}] upload folder [{}] by path '{}'", username, name, path);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't upload folder");
        }
    }

    public void removeFile(String username, String path) {
        log.info("user [{}] try to remove file...", username);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(username + "/" + path)
                            .build());
            log.info("user [{}] remove [{}]", username, path);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't remove file");
        }
    }

    public void removeFolder(String username, String path) {
        log.info("user [{}] try to remove folder...", username);
        try {
            List<Item> results = getAllByPath(username, path);
            for (Item item : results) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(item.objectName())
                        .build());
            }
            log.info("user [{}] remove [{}]", username, path);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't remove folder");
        }
    }

    public List<Item> getAllByFolder(String username, String folderPath) {
        log.info("user [{}] try to get all by folder...", username);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(username + "/" + folderPath)
                        .recursive(false)
                        .build());
        log.info("user [{}] get all objects by folder [{}]", username, folderPath);
        return extractItems(results);
    }

    public List<Item> getAllByPath(String username, String path) {
        log.info("user [{}] try to get all by path...", username);
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(username + "/" + path)
                        .recursive(true)
                        .build());
        log.info("user [{}] get all objects by path [{}]", username, path);
        return extractItems(results);
    }

    public void copyObject(String username, String currentPath, String newPath) {
        log.info("user [{}] try to copy FROM [{}] TO [{}]", username, currentPath, newPath);
        try {
            minioClient.copyObject(CopyObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(username + "/" + newPath)
                    .source(CopySource
                            .builder()
                            .bucket(bucketName)
                            .object(username + "/" + currentPath)
                            .build())
                    .build());
            log.info("success copy FROM [{}] TO [{}]", currentPath, newPath);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't copy file");
        }
    }

    public InputStream downloadFile(String username, String filePath) {
        log.info("user [{}] try to download file [{}]...", username, filePath);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(username + "/" + filePath)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new FileNotFoundException(String.format("file [%s] not found", filePath));
        }
    }

    public List<Item> getAllByUser(String username) {
        List<Item> allUserItems = new ArrayList<>();
        itemsRecursiveSearch(username, "", allUserItems);
        return allUserItems;
    }

    private List<Item> extractItems(Iterable<Result<Item>> results) {
        List<Item> objectsList = new ArrayList<>();
        for (Result<Item> result : results) {
            try {
                objectsList.add(result.get());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                     NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                     InvalidKeyException e) {
                throw new StorageException("Can't extract items");
            }
        }
        return objectsList;
    }

    private void itemsRecursiveSearch(String username, String path, List<Item> allItems) {
        List<Item> results = getAllByFolder(username, path);
        for (Item item : results) {
            if (!(!item.isDir() && item.objectName().endsWith("/"))) {
                allItems.add(item);
            }
            if (item.isDir()) {
                String nextPath = item.objectName().substring(username.length() + 1);
                itemsRecursiveSearch(username, nextPath, allItems);
            }
        }
    }
}
