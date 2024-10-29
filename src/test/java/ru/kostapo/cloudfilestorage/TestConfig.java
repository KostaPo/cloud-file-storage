package ru.kostapo.cloudfilestorage;

import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration
public class TestConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
                .withReuse(true)
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("init.sql"),
                        "/docker-entrypoint-initdb.d/init.sql");
    }

    @Bean
    public MinIOContainer minioContainer() {
        return new MinIOContainer("minio/minio").withReuse(true);
    }

    @Bean
    public MinioClient minioClient(MinIOContainer container) {
     return MinioClient
             .builder()
             .endpoint(container.getS3URL())
             .credentials(container.getUserName(), container.getPassword())
             .build();
    }
}
