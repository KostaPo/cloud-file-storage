package ru.kostapo.cloudfilestorage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TestRegistrationController {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Test
    @Order(1)
    @DisplayName("GET '/registration' [200ok] для не авторизованных")
    @WithAnonymousUser
    public void test01_nonAuthUserGetRequestRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(2)
    @DisplayName("GET '/registration' [redirect => '/'] для авторизованных")
    @WithMockUser
    public void test02_authUserGetRequestRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @Order(3)
    @DisplayName("POST '/registration' [redirect => login] для VALID и UNIQ данных")
    public void test03_goodDataRequestRegistration() throws Exception {
        MockHttpServletRequestBuilder request = post("/registration")
                .param("username", "user")
                .param("password", "pass");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @Order(4)
    @DisplayName("POST '/registration' [422 unprocessable entity] для NON_VALID данных")
    public void test04_nonValidDataRequestRegistration() throws Exception {
        MockHttpServletRequestBuilder request = post("/registration")
                .param("username", "usr")
                .param("password", "p"); // <== NON VALID BAD PARAM

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Order(5)
    @DisplayName("POST '/registration' [409 conflict] для NON_UNIQ данных")
    public void test05_nonUniqDataRequestRegistration() throws Exception {
        // регистрируем юзера
        mockMvc.perform(post("/registration")
                .param("username", "usr")
                .param("password", "pwd"));

        // запрос на повторную регистрацию этого юзера
        MockHttpServletRequestBuilder request = post("/registration")
                .param("username", "usr")
                .param("password", "pwd");

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }
}
