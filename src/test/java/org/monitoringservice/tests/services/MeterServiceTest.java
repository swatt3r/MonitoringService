package org.monitoringservice.tests.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.Reading;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.Date;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Класс тестов для сервиса, который работает со счетчиками и их показаниями.
 */
@ExtendWith(MockitoExtension.class)
class MeterServiceTest {
    /**
     * Поле с сервисом.
     */
    @InjectMocks
    private MeterService meterService;
    /**
     * Mock репозитория пользователей.
     */
    @Mock
    private UserRepository userRepo;
    /**
     * Mock репозитория счетчиков и показаний.
     */
    @Mock
    private MeterRepository meterRepository;

    /**
     * LinkedList пользователей для теста.
     */
    private LinkedList<User> users;

    /**
     * LinkedList истории показаний для теста.
     */
    private LinkedList<Reading> history;
    /**
     * LinkedList всех типов счетчиков для теста.
     */
    private LinkedList<String> types;


    @Test
    @DisplayName("Тест получения актуальных показаний пользователя.")
    void userActualTest() {
        Mockito.when(meterRepository.findUserActualHistory(2))
                .thenReturn(history);
        LinkedList<String> user1Actual = meterService.getUserActual(users.get(1));
        assertThat(user1Actual.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения актуальных показаний пользователей для администратора.")
    void getAdminActualTest() {
        Mockito.when(meterRepository.findUserActualHistory(2))
                .thenReturn(history);

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("user")))
                .thenReturn(users.get(1));

        LinkedList<String> allActual = meterService.getActualForAdmin("user");
        assertThat(allActual.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Тест добавления счетчика пользователю. Такого типа счетчика нет.")
    void addMeterToUserTest_NoSuchMeter() {
        Mockito.when(meterRepository.findAllMetersTypes())
                .thenReturn(types);
        assertThatThrownBy(() ->
                meterService.addNewMeterToUser(users.get(1), "Electricity"))
                .isInstanceOf(MeterAddException.class)
                .hasMessageContaining("Такого типа счетчика нет в системе!");
    }

    @Test
    @DisplayName("Тест добавления счетчика пользователю. Пользователь уже имеет такой тип счетчика.")
    void addMeterToUserTest_AlreadyHaveMeter() {
        Mockito.when(meterRepository.findAllMetersTypes())
                .thenReturn(types);

        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        assertThatThrownBy(() ->
                meterService.addNewMeterToUser(users.get(1), "Heat"))
                .isInstanceOf(MeterAddException.class)
                .hasMessageContaining("Такой счетчик уже есть!");
    }

    @Test
    @DisplayName("Тест успешного добавления счетчика пользователю.")
    void addMeterToUserTest_success() {
        Mockito.when(meterRepository.findAllMetersTypes())
                .thenReturn(types);

        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        try {
            meterService.addNewMeterToUser(users.get(1), "ColdWater");
        } catch (MeterAddException e) {
            Assertions.fail("Счетчик не добавился!");
        }
    }

    @Test
    @DisplayName("Тест получения счетчиков пользователя.")
    void getUserMetersTest() {
        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        LinkedList<String> meters = meterService.getUserMeters(users.get(1));
        assertThat(meters.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения истории показаний счетчиков пользователя.")
    void getUserHistoryTest() {
        Mockito.when(meterRepository.findHistoryByUserId(2))
                .thenReturn(history);

        LinkedList<String> history = meterService.getUserHistory(users.get(1));
        assertThat(history.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователей для администратора.")
    void getAdminHistoryTest() {
        Mockito.when(meterRepository.findHistoryByUserId(2))
                .thenReturn(history);

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("user")))
                .thenReturn(users.get(1));

        LinkedList<String> specUserHistory = meterService.getHistoryForAdmin("user");
        assertThat(specUserHistory.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Тест получения истории показаний счетчиков пользователя за месяц.")
    void getUserMonthHistoryTest() {
        Mockito.when(meterRepository.findMonthHistoryByUserId(2, 12))
                .thenReturn(history);

        LinkedList<String> history = meterService.getUserMonthHistory(users.get(1), 12);
        assertThat(history.size()).isEqualTo(1);

        history = meterService.getUserMonthHistory(users.get(1), -1);
        assertThat(history.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователей за месяц для администратора.")
    void getAdminMonthHistoryTest() {
        Mockito.when(meterRepository.findMonthHistoryByUserId(2, 12))
                .thenReturn(history);

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("user")))
                .thenReturn(users.get(1));

        LinkedList<String> specUserHistory = meterService.getMonthHistoryForAdmin("user", 12);
        assertThat(specUserHistory.size()).isEqualTo(1);

        LinkedList<String> allHistory = meterService.getMonthHistoryForAdmin("user", -1);
        assertThat(allHistory.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Тест внесения нового показания. Неверное показание счетчика.")
    void newReadoutTest_invalidValue() {
        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        assertThatThrownBy(() ->
                meterService.newReadout(users.get(1), "Heat", -10))
                .isInstanceOf(ReadoutException.class)
                .hasMessageContaining("Неверное показание счетчика!");
    }

    @Test
    @DisplayName("Тест внесения нового показания. Неверный месяц.")
    void newReadoutTest_invalidMonth() {
        LinkedList<Reading> userHistory = new LinkedList<>();
        userHistory.add(new Reading(2, "Heat", new Date(1707065711000L), 56));

        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        Mockito.when(meterRepository.findUserActualHistory(2))
                .thenReturn(userHistory);
        assertThatThrownBy(() ->
                meterService.newReadout(users.get(1), "Heat", 57))
                .isInstanceOf(ReadoutException.class)
                .hasMessageContaining("Запись в этом месяце уже есть!");
    }

    @Test
    @DisplayName("Тест внесения нового показания. Неверный тип счетчика.")
    void newReadoutTest_invalidMeter() {
        assertThatThrownBy(() ->
                meterService.newReadout(users.get(1), "Electricity", 50))
                .isInstanceOf(ReadoutException.class)
                .hasMessageContaining("Такой тип счетчика не зарегистрирован!");
    }

    @Test
    @DisplayName("Тест успешного внесения нового показания.")
    void newReadoutTest_success() {
        LinkedList<String> userTypes = new LinkedList<>();
        userTypes.add("Heat");
        Mockito.when(meterRepository.findMetersByUserId(2))
                .thenReturn(userTypes);

        try {
            meterService.newReadout(users.get(1), "Heat", 57);
        } catch (ReadoutException e) {
            Assertions.fail("Показание неверно!");
        }
    }

    /**
     * Метод создания списка пользователей для тестов.
     */
    @BeforeEach
    public void getUsers() {
        users = new LinkedList<>();
        history = new LinkedList<>();
        types = new LinkedList<>();

        User user1 = new User(1, "admin", "admin", Role.ADMIN, "", "", -1, -1);
        User user2 = new User(2, "user", "user", Role.USER, "Moscow", "Red", 12, 15);
        users.add(user1);
        users.add(user2);

        history.add(new Reading(2, "Heat", new Date(1702929600000L), 56));

        types.add("Heat");
        types.add("HotWater");
        types.add("ColdWater");
    }
}
