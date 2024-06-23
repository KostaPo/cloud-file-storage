package ru.kostapo.cloudfilestorage.repository;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.exception.StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Repository
public class MinioRepository {

    @Value("${minio.client.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @PostConstruct
    public void createAppRootBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.info("create MinIO bucket with name [{}]", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            log.info("MinIO bucket [{}] already created", bucketName);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new RuntimeException("Storage service can't create root bucket");
        }
    }

    public void uploadFile(String username, String dirPath, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(stream, file.getSize(), -1)
                    .bucket(bucketName)
                    .object(username + "/" + file.getOriginalFilename())
                    .build());
            log.info("correct upload file '{}' ", file.getOriginalFilename());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't upload file");
        }
    }

    public void uploadFolder(String username, String dirPath, String dirName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(username + "/")
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new StorageException("Storage service can't upload folder");
        }
    }

    public List<Item> getAllByPath(String userName, String path) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(userName + "/")
                            .recursive(false)
                            .build());
            return extractItems(results);
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
}
