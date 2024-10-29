package ru.kostapo.cloudfilestorage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import ru.kostapo.cloudfilestorage.entity.AppUser;
import ru.kostapo.cloudfilestorage.entity.dto.MinIoResObject;
import ru.kostapo.cloudfilestorage.mapper.ObjectMapper;
import ru.kostapo.cloudfilestorage.service.MinIoService;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMinioStorage extends BaseIntegrationTest {

    @Autowired
    private MinIoService minIoService;

    @Test
    @Order(1)
    @DisplayName("Проверка созданной корзины")
    public void testBucketExists() {
        boolean exists = minIoService.isBucketExists("root");
        assertTrue(exists);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка загрузки файла")
    public void testFileObjectUpload() {
        String username = "testuser";
        String path = "testpath";
        String filename = "test.txt";

        MockMultipartFile mockFile = new MockMultipartFile("testfile", filename,
                MediaType.TEXT_PLAIN_VALUE, "TestText".getBytes());
        MockMultipartFile[] mockFilesList = new MockMultipartFile[]{mockFile};

        AppUser user = mock(AppUser.class);
        when(user.getUsername()).thenReturn(username);

        minIoService.uploadFiles(
                ObjectMapper.INSTANCE.multipartFilesToMinIoObjectList(user.getUsername(), mockFilesList),
                String.format("%s/", path));

        boolean exists = minIoService.isObjectExists(String.format("%s/%s/%s", username, path, filename));
        assertTrue(exists);
    }

    @Test
    @Order(3)
    @DisplayName("Проверка скачивания файла")
    public void testFileObjectDownload() {
        String username = "testuser";
        String path = "testpath";
        String filename = "test.txt";

        MinIoResObject fileObject = new MinIoResObject();
        fileObject.setItIsDir(false);
        fileObject.setFullPath(String.format("%s/", path));
        fileObject.setObjectName(filename);

        InputStreamResource objectBytesStream = minIoService.downloadObject(username, fileObject);

        assertNotNull(objectBytesStream);

        try (InputStream inputStream = objectBytesStream.getInputStream()) {
            byte[] downloadedFileContent = inputStream.readAllBytes();
            assertArrayEquals("TestText".getBytes(), downloadedFileContent);
        } catch (IOException e) {
            fail("Failed to read from InputStream: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Проверка переименования файла")
    public void testFileObjectRename() {
        String username = "testuser";
        String path = "testpath";
        String oldFilename = "test.txt";
        String newFilename = "newtestname";

        MinIoResObject fileObject = new MinIoResObject();
        fileObject.setItIsDir(false);
        fileObject.setFullPath(String.format("%s/", path));
        fileObject.setObjectName(oldFilename);

        //после предыдущих тестов файл находится в контейнере
        boolean fileOldNameExistsBefore =
                minIoService.isObjectExists(String.format("%s/%s/%s", username, path, oldFilename));
        assertTrue(fileOldNameExistsBefore);

        minIoService.renameObject(username, fileObject, newFilename);

        //после переименования файла с прежним именем нет
        assertThrows(ru.kostapo.cloudfilestorage.exception.ObjectNotFoundException.class, () -> {
            minIoService.isObjectExists(String.format("%s/%s/%s", username, path, oldFilename));
        });

        //после переименования есть файл с новым именем
        boolean fileNewNameExists =
                minIoService.isObjectExists(String.format("%s/%s/%s.txt", username, path, newFilename));
        assertTrue(fileNewNameExists);
    }

    @Test
    @Order(5)
    @DisplayName("Проверка удаления файла")
    public void testFileObjectDelete() {
        String username = "testuser";
        String path = "testpath";
        String filename = "test.txt";

        MockMultipartFile mockFile = new MockMultipartFile("testfile", filename,
                MediaType.TEXT_PLAIN_VALUE, "TestText".getBytes());
        MockMultipartFile[] mockFilesList = new MockMultipartFile[]{mockFile};

        //ЗАГРУЖАЕМ
        minIoService.uploadFiles(
                ObjectMapper.INSTANCE.multipartFilesToMinIoObjectList(username, mockFilesList),
                String.format("%s/", path));
        //ПРОВЕРЯЕМ
        boolean exists = minIoService.isObjectExists(String.format("%s/%s/%s", username, path, filename));
        assertTrue(exists);

        MinIoResObject fileObject = new MinIoResObject();
        fileObject.setItIsDir(false);
        fileObject.setFullPath(String.format("%s/", path));
        fileObject.setObjectName(filename);

        //УДАЛЯЕМ
        minIoService.deleteObject(username, fileObject);

        //ПРОВЕРЯЕМ
        assertThrows(ru.kostapo.cloudfilestorage.exception.ObjectNotFoundException.class, () -> {
            minIoService.isObjectExists(String.format("%s/%s/%s", username, path, filename));
        });
    }
}
