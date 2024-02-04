package org.monitoringservice.tests.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.util.MigrationUtil;
import org.monitoringservice.util.PropertiesUtil;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.LinkedList;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс тестов репозитория пользователей.
 */
@Testcontainers
public class UserRepositoryTest {
    /**
     * Поле с конфигурациями приложения.
     */
    private static final Properties properties = PropertiesUtil.getApplicationProperties();
    /**
     * Поле с контейнером БД.
     */
    private static final PostgreSQLContainer<?> bdContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("monitorService")
                    .withUsername(properties.getProperty("username"))
                    .withPassword(properties.getProperty("password"));
    /**
     * Поле с репозиторием.
     */
    private UserRepository userRepository;

    /**
     * Метод иницализации БД контейнера и репозитория.
     */
    @BeforeEach
    void initRepository() {
        bdContainer.start();
        MigrationUtil.migrateDB(properties);

        userRepository = new UserRepository(properties);
    }

    /**
     * Метод закрытия БД контейнера.
     */
    @AfterEach
    void closeContainer() {
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
                        "user3",
                        "user3",
                        Role.USER,
                        "Test",
                        "Test",
                        12,
                        15)
        );
        User getUser = userRepository.findUserByFullAddress("Test", "Test", 12, 15);
        assertThat(getUser.getLogin()).isEqualTo("user3");
        assertThat(getUser.getRole().toString()).isEqualTo("USER");
        assertThat(getUser.getPassword()).isEqualTo("user3");
    }

    @Test
    @DisplayName("Тест получения всех пользователей")
    void FindAllUsersTest() {
        LinkedList<User> users = userRepository.findAllUsers();
        assertThat(users.size()).isGreaterThanOrEqualTo(1);
    }
}
