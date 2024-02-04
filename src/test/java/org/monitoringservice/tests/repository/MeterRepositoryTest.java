package org.monitoringservice.tests.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.monitoringservice.entities.Reading;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.util.MigrationUtil;
import org.monitoringservice.util.PropertiesUtil;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс тестов репозитория счетчиков и показаний.
 */
@Testcontainers
public class MeterRepositoryTest {
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
    private MeterRepository meterRepository;

    /**
     * Метод иницализации БД контейнера и репозитория.
     */
    @BeforeEach
    void initRepository() {
        bdContainer.start();
        MigrationUtil.migrateDB(properties);

        meterRepository = new MeterRepository(properties);
    }

    /**
     * Метод закрытия БД контейнера.
     */
    @AfterEach
    void closeContainer() {
        bdContainer.close();
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователя")
    void findUserHistoryTest() {
        LinkedList<Reading> userHistory = meterRepository.findHistoryByUserId(2);
        assertThat(userHistory.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения актуальных счетчиков пользователя")
    void saveAndFindUserTest() {
        LinkedList<String> meters = meterRepository.findMetersByUserId(2);
        assertThat(meters.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения всех типов счетчиков")
    void findAllMeterTypesTest() {
        LinkedList<String> types = meterRepository.findAllMetersTypes();
        assertThat(types.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователя за месяц")
    void findUserMonthHistoryTest() {
        LinkedList<Reading> history = meterRepository.findMonthHistoryByUserId(2, 12);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
        Reading read = new Reading(2, "Heat", new Date(1702929600000L), 56);
        assertThat(history.contains(read))
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Тест добавления нового типа счетчика")
    void addNewMeterTypeTest() {
        meterRepository.addNewType("TestType");
        LinkedList<String> typesAfter = meterRepository.findAllMetersTypes();
        assertThat(typesAfter.contains("TestType")).isEqualTo(true);
    }

    @Test
    @DisplayName("Тест регистрации нового типа счетчика на пользователя")
    void addNewTypeToUserTest() {
        meterRepository.addNewType("TestType");
        meterRepository.addNewTypeToUser(2,"TestType");
        LinkedList<String> typesAfter = meterRepository.findMetersByUserId(2);
        assertThat(typesAfter.contains("TestType")).isEqualTo(true);
    }

    @Test
    @DisplayName("Тест получения актуальной истории показаний пользователя")
    void findUserActualHistory() {
        LinkedList<Reading> history = meterRepository.findUserActualHistory(2);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
    }
}
