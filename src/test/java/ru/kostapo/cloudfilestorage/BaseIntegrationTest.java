package ru.kostapo.cloudfilestorage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = TestConfig.class)
abstract class BaseIntegrationTest {

    @Autowired
    private PostgreSQLContainer<?> postgres;

    @Autowired
    private MinIOContainer minio;
}
