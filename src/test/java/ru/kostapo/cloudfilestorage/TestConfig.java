package ru.kostapo.cloudfilestorage;

import com.zaxxer.hikari.HikariDataSource;
import io.minio.MinioClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"));
    }

    @Bean
    DataSource dataSource(PostgreSQLContainer<?> postgreSQLContainer) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        hikariDataSource.setUsername(postgreSQLContainer.getUsername());
        hikariDataSource.setPassword(postgreSQLContainer.getPassword());
        return hikariDataSource;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MinIOContainer minIOContainer() {
        return new MinIOContainer("minio/minio");
    }

    @Bean
    public MinioClient minioClient(MinIOContainer minIOContainer) {
        return MinioClient
                .builder()
                .endpoint(minIOContainer.getS3URL())
                .credentials(minIOContainer.getUserName(), minIOContainer.getPassword())
                .build();
    }
}
