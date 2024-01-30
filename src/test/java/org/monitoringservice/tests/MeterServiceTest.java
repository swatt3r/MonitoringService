package org.monitoringservice.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.MeterType;
import org.monitoringservice.entities.Reading;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.Arrays;
import java.util.HashMap;
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
     * Mock репозитория.
     */
    @Mock
    private UserRepository userRepo;

    /**
     * Map пользователей для теста.
     */
    private HashMap<Integer, User> users;

    @Test
    @DisplayName("Тест получения актуальных показаний пользователя.")
    void userActualTest() {
        LinkedList<String> user1Actual = meterService.getUserActual(users.get(1));
        assertThat(user1Actual.size()).isEqualTo(1);
        assertThat(user1Actual.get(0)).isEqualTo("Heat, 19.1.2024 - 45");

        LinkedList<String> user2Actual = meterService.getUserActual(users.get(2));
        assertThat(user2Actual.size()).isEqualTo(2);
        assertThat(user2Actual.get(0)).isEqualTo("ColdWater, 17.11.2023 - 15");
    }

    @Test
    @DisplayName("Тест получения актуальных показаний пользователей для администратора.")
    void getAdminActualTest() {
        Mockito.when(userRepo.getUsers()).thenReturn(users);

        LinkedList<String> allActual = meterService.getActualForAdmin("");
        assertThat(allActual.size()).isEqualTo(3);
        assertThat(allActual.get(0)).isEqualTo("user, Heat, 19.1.2024 - 45");

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(users.get(2));

        LinkedList<String> specUserActual = meterService.getActualForAdmin("newUser");
        assertThat(specUserActual.size()).isEqualTo(2);
        assertThat(specUserActual.get(1)).isEqualTo("newUser, Heat, 17.11.2023 - 90");
    }

    @Test
    @DisplayName("Тест добавления счетчика пользователю. Такого типа счетчика нет.")
    void addMeterToUserTest_NoSuchMeter() {
        assertThatThrownBy(() ->
                meterService.addNewMeterToUser(users.get(1), "Electricity"))
                .isInstanceOf(MeterAddException.class)
                .hasMessageContaining("Такого типа счетчика нет в системе!");
    }

    @Test
    @DisplayName("Тест добавления счетчика пользователю. Пользователь уже имеет такой тип счетчика.")
    void addMeterToUserTest_AlreadyHaveMeter() {
        assertThatThrownBy(() ->
                meterService.addNewMeterToUser(users.get(1), "Heat"))
                .isInstanceOf(MeterAddException.class)
                .hasMessageContaining("Такой счетчик уже есть!");
    }

    @Test
    @DisplayName("Тест успешного добавления счетчика пользователю.")
    void addMeterToUserTest_success() {
        try {
            meterService.addNewMeterToUser(users.get(1), "ColdWater");
        } catch (MeterAddException e) {
            Assertions.fail("Счетчик не добавился!");
        }
    }

    @Test
    @DisplayName("Тест получения счетчиков пользователя.")
    void getUserMetersTest() {
        LinkedList<String> meters = meterService.getUserMeters(users.get(2));
        assertThat(meters.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Тест получения истории показаний счетчиков пользователя.")
    void getUserHistoryTest() {
        LinkedList<String> history = meterService.getUserHistory(users.get(2));
        assertThat(history.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователей для администратора.")
    void getAdminHistoryTest() {
        Mockito.when(userRepo.getUsers()).thenReturn(users);

        LinkedList<String> allHistory = meterService.getHistoryForAdmin("");
        assertThat(allHistory.size()).isEqualTo(6);
        assertThat(allHistory.get(0)).isEqualTo("user, Heat, 17.11.2023 - 34");


        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(users.get(2));

        LinkedList<String> specUserHistory = meterService.getHistoryForAdmin("newUser");
        assertThat(specUserHistory.size()).isEqualTo(3);
        assertThat(specUserHistory.get(2)).isEqualTo("newUser, Heat, 17.11.2023 - 90");
    }

    @Test
    @DisplayName("Тест получения истории показаний счетчиков пользователя за месяц.")
    void getUserMonthHistoryTest() {
        LinkedList<String> history = meterService.getUserMonthHistory(users.get(2), 11);
        assertThat(history.size()).isEqualTo(2);

        history = meterService.getUserMonthHistory(users.get(2), -1);
        assertThat(history.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Тест получения истории показаний пользователей за месяц для администратора.")
    void getAdminMonthHistoryTest() {
        Mockito.when(userRepo.getUsers()).thenReturn(users);

        LinkedList<String> allHistory = meterService.getMonthHistoryForAdmin("", 11);
        assertThat(allHistory.size()).isEqualTo(3);
        assertThat(allHistory.get(0)).isEqualTo("user, Heat, 17.11.2023 - 34");


        allHistory = meterService.getMonthHistoryForAdmin("", -1);
        assertThat(allHistory.size()).isEqualTo(0);


        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(users.get(2));

        LinkedList<String> specUserHistory = meterService.getMonthHistoryForAdmin("newUser", 10);
        assertThat(specUserHistory.size()).isEqualTo(1);
        assertThat(specUserHistory.get(0)).isEqualTo("newUser, Heat, 15.10.2023 - 70");

        allHistory = meterService.getMonthHistoryForAdmin("newUser", -1);
        assertThat(allHistory.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Тест внесения нового показания. Неверное показание счетчика.")
    void newReadoutTest_invalidValue() {
        assertThatThrownBy(() ->
                meterService.newReadout(users.get(1), "Heat", 10))
                .isInstanceOf(ReadoutException.class)
                .hasMessageContaining("Неверное показание счетчика!");

        assertThatThrownBy(() ->
                meterService.newReadout(users.get(2), "ColdWater", 14))
                .isInstanceOf(ReadoutException.class)
                .hasMessageContaining("Неверное показание счетчика!");
    }

    @Test
    @DisplayName("Тест внесения нового показания. Неверный месяц.")
    void newReadoutTest_invalidMonth() {
        assertThatThrownBy(() ->
                meterService.newReadout(users.get(1), "Heat", 50))
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
        try {
            meterService.newReadout(users.get(2), "ColdWater", 56);
        } catch (ReadoutException e) {
            Assertions.fail("Показание неверно!");
        }
    }

    /**
     * Метод создания списка пользователей для тестов.
     */
    @BeforeEach
    public void getUsers() {
        users = new HashMap<>();
        User user1 = new User("admin", "admin", Role.ADMIN, "", "", -1, -1);


        User user2 = new User("user", "user", Role.USER, "city", "str", 11, 12);
        Reading[] readings = new Reading[]{
                new Reading(17, 11, 2023, 34),
                new Reading(18, 12, 2023, 39),
                new Reading(19, 1, 2024, 45)
        };
        user2.getHistory().put(new MeterType("Heat"), new LinkedList<>(Arrays.asList(readings)));


        readings = new Reading[]{
                new Reading(17, 11, 2023, 15)
        };
        User user3 = new User("newUser", "user", Role.USER, "city", "str", 10, 5);
        user3.getHistory().put(new MeterType("ColdWater"), new LinkedList<>(Arrays.asList(readings)));
        readings = new Reading[]{
                new Reading(15, 10, 2023, 70),
                new Reading(17, 11, 2023, 90)
        };
        user3.getHistory().put(new MeterType("Heat"), new LinkedList<>(Arrays.asList(readings)));

        users.put(0, user1);
        users.put(1, user2);
        users.put(2, user3);
    }
}
