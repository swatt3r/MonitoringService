package org.monitoringservice.tests.repository;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.monitoringservice.entities.MeterReading;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.util.PropertiesUtil;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс тестов репозитория счетчиков и показаний.
 */
@Testcontainers
public class MeterRepositoryTest {
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
    private static MeterRepository meterRepository;

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

        meterRepository = new MeterRepository(dataSource);
    }

    /**
     * Метод закрытия БД контейнера.
     */
    @AfterAll
    static void closeContainer() {
        bdContainer.close();
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователя")
    void findUserHistoryTest() {
        List<MeterReading> userHistory = meterRepository.findHistoryByUserId(64);
        assertThat(userHistory.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения актуальных счетчиков пользователя")
    void saveAndFindUserTest() {
        List<String> meters = meterRepository.findMetersByUserId(47);
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
        List<MeterReading> history = meterRepository.findMonthHistoryByUserId(47, 12);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
        MeterReading read = new MeterReading(47, "Heat", LocalDate.of(2023, 12, 19), 56);
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
    @DisplayName("Тест получения актуальной истории показаний пользователя")
    void findUserActualHistory() {
        List<MeterReading> history = meterRepository.findUserActualHistory(47);
        assertThat(history.size()).isGreaterThanOrEqualTo(1);
    }
}
