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

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
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
            throw new RuntimeException("Storage service doesn't answer");
        }
    }

    public void uploadFile(String username, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(stream, file.getSize(), -1)
                    .bucket(bucketName)
                    .object(username + "/" + file.getOriginalFilename())
                    .build());
            log.info("correct upload file '{}' ", file.getOriginalFilename());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getAll(String userName) {
        try {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(userName)
                    .recursive(true)
                    .build();
            Iterable<Result<Item>> results = minioClient.listObjects(args);
            List<String> fileList = new LinkedList<>();
            List<String> directoryList = new LinkedList<>();

            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir()) {
                    directoryList.add(item.objectName());
                } else {
                    fileList.add(item.objectName());
                }
            }

            System.out.println("Files:");
            for (String file : fileList) {
                System.out.println(file);
            }

            System.out.println("\nDirectories:");
            for (String directory : directoryList) {
                System.out.println(directory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
