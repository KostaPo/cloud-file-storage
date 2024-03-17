package ru.kostapo.cloudfilestorage.repository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Log4j2
@Repository
public class MinioRepository {

    @Value("${minio.client.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    public MinioRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void createAppRootBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 NoSuchAlgorithmException | IOException | ServerException | XmlParserException |
                 InvalidKeyException e) {
            throw new RuntimeException("Storage service doesn't answer");
        }
    }

    public void uploadFile(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(stream, file.getSize(), -1)
                    .bucket(bucketName)
                    .object("test/" + file.getOriginalFilename())
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
//        String originalFilename = file.getOriginalFilename();
//        assert originalFilename != null;
//        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        long fileSize = file.getSize();
//        log.info("Original filename: " + originalFilename);
//        log.info("File extension: " + fileExtension);
//        log.info("File size: " + fileSize);}
}
