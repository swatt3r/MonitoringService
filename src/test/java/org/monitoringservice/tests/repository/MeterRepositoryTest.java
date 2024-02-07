package org.monitoringservice.tests.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.monitoringservice.entities.MeterReading;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.util.MigrationUtil;
import org.monitoringservice.util.PropertiesUtil;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
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

        meterRepository = new MeterRepository();
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
        List<MeterReading> userHistory = meterRepository.findHistoryByUserId(2);
        assertThat(userHistory.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения актуальных счетчиков пользователя")
    void saveAndFindUserTest() {
        List<String> meters = meterRepository.findMetersByUserId(2);
        assertThat(meters.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения всех типов счетчиков")
    void findAllMeterTypesTest() {
        List<String> types = meterRepository.findAllMetersTypes();
        assertThat(types.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователя за месяц")
    void findUserMonthHistoryTest() {
        List<MeterReading> history = meterRepository.findMonthHistoryByUserId(2, 12);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
        MeterReading read = new MeterReading(2, "Heat", LocalDate.of(2023,12,19), 56);
        assertThat(history.contains(read))
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Тест добавления нового типа счетчика")
    void addNewMeterTypeTest() {
        meterRepository.addNewType("TestType");
        List<String> typesAfter = meterRepository.findAllMetersTypes();
        assertThat(typesAfter.contains("TestType")).isEqualTo(true);
    }

    @Test
    @DisplayName("Тест регистрации нового типа счетчика на пользователя")
    void addNewTypeToUserTest() {
        meterRepository.addNewType("TestType");
        meterRepository.addNewTypeToUser(2,"TestType");
        List<String> typesAfter = meterRepository.findMetersByUserId(2);
        assertThat(typesAfter.contains("TestType")).isEqualTo(true);
    }

    @Test
    @DisplayName("Тест получения актуальной истории показаний пользователя")
    void findUserActualHistory() {
        List<MeterReading> history = meterRepository.findUserActualHistory(2);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
    }
}
