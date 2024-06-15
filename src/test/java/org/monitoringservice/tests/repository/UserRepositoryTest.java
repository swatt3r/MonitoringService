package org.monitoringservice.tests.repository;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.util.PropertiesUtil;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс тестов репозитория пользователей.
 */
@Testcontainers
public class UserRepositoryTest {
    /**
     * Поле с конфигурациями приложения.
     */
    private static final Map<String, Object> properties = PropertiesUtil.getApplicationProperties();
    /**
     * Поле с контейнером БД.
     */
    private static final PostgreSQLContainer<?> bdContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("monitorService")
                    .withUsername((String) properties.get("username"))
                    .withPassword((String) properties.get("password"));
    /**
     * Поле с репозиторием.
     */
    private static UserRepository userRepository;

    /**
     * Метод иницализации БД контейнера и репозитория.
     */
    @BeforeAll
    static void initRepository() {
        bdContainer.start();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(bdContainer.getDriverClassName());
        dataSource.setUrl((String) properties.get("url"));
        dataSource.setUsername(bdContainer.getUsername());
        dataSource.setPassword(bdContainer.getPassword());

        userRepository = new UserRepository(dataSource);
    }

    /**
     * Метод закрытия БД контейнера.
     */
    @AfterAll
    static void closeContainer() {
        bdContainer.close();
    }

    @Test
    @DisplayName("Тест сохранения пользователя в БД и последующего его получения")
    void saveAndFindUserTest() {
        userRepository.save(
                new User(-1,
                        "user2",
                        "user2",
                        Role.USER,
                        "Test",
                        "Test",
                        12,
                        15)
        );
        User getUser = userRepository.findUserByLogin("user2");
        assertThat(getUser.getLogin()).isEqualTo("user2");
        assertThat(getUser.getRole().toString()).isEqualTo("USER");
        assertThat(getUser.getPassword()).isEqualTo("user2");
    }

    @Test
    @DisplayName("Тест получения пользователя по адресу")
    void saveAndFindUserByAddressTest() {
        userRepository.save(
                new User(-1,
                        "user2",
                        "user2",
                        Role.USER,
                        "Test",
                        "Test",
                        12,
                        15)
        );
        User getUser = userRepository.findUserByFullAddress("Test", "Test", 12, 15);
        assertThat(getUser.getLogin()).isEqualTo("user2");
        assertThat(getUser.getRole().toString()).isEqualTo("USER");
        assertThat(getUser.getPassword()).isEqualTo("user2");
    }

    @Test
    @DisplayName("Тест получения всех пользователей")
    void FindAllUsersTest() {
        List<User> users = userRepository.findAllUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(1);
    }
}
