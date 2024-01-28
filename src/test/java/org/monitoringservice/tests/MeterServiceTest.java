package org.monitoringservice.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.*;
import org.monitoringservice.repositories.UserRepo;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Класс тестов для сервиса, который работает со счетчиками и их показаниями.
 */
@ExtendWith(MockitoExtension.class)
class MeterServiceTest {
    /** Поле с сервисом. */
    @InjectMocks
    private MeterService meterService;
    /** Mock репозитория. */
    @Mock
    private UserRepo userRepo;
    /**
     * Тест получения актуальных показаний пользователя.
     */
    @Test
    void userActualTest() {
        ArrayList<User> users = getUsers();

        LinkedList<String> user1Actual = meterService.getUserActual(users.get(1));
        Assertions.assertNotNull(user1Actual);
        Assertions.assertEquals(1, user1Actual.size());
        Assertions.assertEquals("Heat, 19.1.2024 - 45", user1Actual.get(0));

        LinkedList<String> user2Actual = meterService.getUserActual(users.get(2));
        Assertions.assertEquals(2, user2Actual.size());
        Assertions.assertEquals("ColdWater, 17.11.2023 - 15", user2Actual.get(0));
    }
    /**
     * Тест получения актуальных показаний пользователей для администратора.
     */
    @Test
    void getAdminActualTest() {
        Mockito.when(userRepo.getUsers()).thenReturn(getUsers());

        LinkedList<String> allActual = meterService.getActualForAdmin("");
        Assertions.assertEquals(3, allActual.size());
        Assertions.assertEquals("user, Heat, 19.1.2024 - 45", allActual.get(0));

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(getUsers().get(2));

        LinkedList<String> specUserActual = meterService.getActualForAdmin("newUser");
        Assertions.assertEquals(2, specUserActual.size());
        Assertions.assertEquals("newUser, Heat, 17.11.2023 - 90", specUserActual.get(1));
    }
    /**
     * Тест добавления счетчика пользователю. Такого типа счетчика нет.
     */
    @Test
    void addMeterToUserTest_NoSuchMeter() {
        ArrayList<User> users = getUsers();
        MeterAddException thrown = Assertions.assertThrows(
                MeterAddException.class,
                () -> meterService.addNewMeterToUser(users.get(1), "Electricity"));
        Assertions.assertEquals("Такого типа счетчика нет в системе!", thrown.getMessage());
    }
    /**
     * Тест добавления счетчика пользователю. Пользователь уже имеет такой тип счетчика.
     */
    @Test
    void addMeterToUserTest_AlreadyHaveMeter() {
        ArrayList<User> users = getUsers();
        MeterAddException thrown = Assertions.assertThrows(
                MeterAddException.class,
                () -> meterService.addNewMeterToUser(users.get(1), "Heat"));
        Assertions.assertEquals("Такой счетчик уже есть!", thrown.getMessage());
    }
    /**
     * Тест успешного добавления счетчика пользователю.
     */
    @Test
    void addMeterToUserTest_success() {
        ArrayList<User> users = getUsers();
        try {
            meterService.addNewMeterToUser(users.get(1), "ColdWater");
            Assertions.assertEquals(2, users.get(1).getMeters().size());
        } catch (MeterAddException e) {
            Assertions.fail();
        }
    }
    /**
     * Тест получения счетчиков пользователя.
     */
    @Test
    void getUserMetersTest() {
        LinkedList<String> meters = meterService.getUserMeters(getUsers().get(2));
        Assertions.assertEquals(2, meters.size());
    }
    /**
     * Тест получения истории показаний счетчиков пользователя.
     */
    @Test
    void getUserHistoryTest() {
        LinkedList<String> history = meterService.getUserHistory(getUsers().get(2));
        Assertions.assertEquals(3, history.size());
    }
    /**
     * Тест получения истории показаний пользователей для администратора.
     */
    @Test
    void getAdminHistoryTest() {
        ArrayList<User> users = getUsers();
        Mockito.when(userRepo.getUsers()).thenReturn(users);

        LinkedList<String> allHistory = meterService.getHistoryForAdmin("");
        Assertions.assertEquals(6, allHistory.size());
        Assertions.assertEquals("user, Heat, 17.11.2023 - 34", allHistory.get(0));

        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(users.get(2));

        LinkedList<String> specUserHistory = meterService.getHistoryForAdmin("newUser");
        Assertions.assertEquals(3, specUserHistory.size());
        Assertions.assertEquals("newUser, Heat, 17.11.2023 - 90", specUserHistory.get(2));
    }
    /**
     * Тест получения истории показаний счетчиков пользователя за месяц.
     */
    @Test
    void getUserMonthHistoryTest() {
        LinkedList<String> history = meterService.getUserMonthHistory(getUsers().get(2), 11);
        Assertions.assertEquals(2, history.size());

        history = meterService.getUserMonthHistory(getUsers().get(2), -1);
        Assertions.assertEquals(0, history.size());
    }
    /**
     * Тест получения истории показаний пользователей за месяц для администратора.
     */
    @Test
    void getAdminMonthHistoryTest() {
        ArrayList<User> users = getUsers();
        Mockito.when(userRepo.getUsers()).thenReturn(users);

        LinkedList<String> allHistory = meterService.getMonthHistoryForAdmin("", 11);
        Assertions.assertEquals(3, allHistory.size());
        Assertions.assertEquals("user, Heat, 17.11.2023 - 34", allHistory.get(0));

        allHistory = meterService.getMonthHistoryForAdmin("", -1);
        Assertions.assertEquals(0, allHistory.size());


        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(users.get(2));

        LinkedList<String> specUserHistory = meterService.getMonthHistoryForAdmin("newUser", 10);
        Assertions.assertEquals(1, specUserHistory.size());
        Assertions.assertEquals("newUser, Heat, 15.10.2023 - 70", specUserHistory.get(0));

        allHistory = meterService.getMonthHistoryForAdmin("newUser", -1);
        Assertions.assertEquals(0, allHistory.size());
    }
    /**
     * Тест внесения нового показания. Неверное показание счетчика.
     */
    @Test
    void newReadoutTest_invalidValue() {
        ReadoutException thrown = Assertions.assertThrows(
                ReadoutException.class,
                () -> meterService.newReadout(getUsers().get(1), "Heat", 10));
        Assertions.assertEquals("Неверное показание счетчика!", thrown.getMessage());

        thrown = Assertions.assertThrows(
                ReadoutException.class,
                () -> meterService.newReadout(getUsers().get(2), "ColdWater", 14));
        Assertions.assertEquals("Неверное показание счетчика!", thrown.getMessage());
    }
    /**
     * Тест внесения нового показания. Неверный месяц.
     */
    @Test
    void newReadoutTest_invalidMonth() {
        ReadoutException thrown = Assertions.assertThrows(
                ReadoutException.class,
                () -> meterService.newReadout(getUsers().get(1), "Heat", 50));
        Assertions.assertEquals("Запись в этом месяце уже есть!", thrown.getMessage());
    }
    /**
     * Тест внесения нового показания. Неверный тип счетчика.
     */
    @Test
    void newReadoutTest_invalidMeter() {
        ReadoutException thrown = Assertions.assertThrows(
                ReadoutException.class,
                () -> meterService.newReadout(getUsers().get(1), "Electricity", 50));
        Assertions.assertEquals("Такой тип счетчика не зарегистрирован!", thrown.getMessage());
    }
    /**
     * Тест успешного внесения нового показания.
     */
    @Test
    void newReadoutTest_success() {
        try {
            meterService.newReadout(getUsers().get(2), "ColdWater", 56);
            Assertions.assertEquals(2, getUsers().get(2).getMeters().get(1).getHistory().size());
        } catch (ReadoutException e) {
            Assertions.fail();
        }
    }

    /**
     * Метод создания списка пользователей для тестов.
     * @return ArrayList - список пользователей для теста
     */
    private ArrayList<User> getUsers() {
        ArrayList<User> result = new ArrayList<>(3);
        User user1 = new User("admin", "admin", Role.ADMIN, "", "", -1, -1);
        User user2 = new User("user", "user", Role.USER, "city", "str", 11, 12);
        user2.getMeters().add(new Meter(new MeterType("Heat")));
        user2.getMeters().get(0).getHistory().add(new Reading(17, 11, 2023, 34));
        user2.getMeters().get(0).getHistory().add(new Reading(18, 12, 2023, 39));
        user2.getMeters().get(0).getHistory().add(new Reading(19, 1, 2024, 45));
        User user3 = new User("newUser", "user", Role.USER, "city", "str", 10, 5);
        user3.getMeters().add(new Meter(new MeterType("ColdWater")));
        user3.getMeters().get(0).getHistory().add(new Reading(17, 11, 2023, 15));
        user3.getMeters().add(new Meter(new MeterType("Heat")));
        user3.getMeters().get(1).getHistory().add(new Reading(15, 10, 2023, 70));
        user3.getMeters().get(1).getHistory().add(new Reading(17, 11, 2023, 90));
        result.add(user1);
        result.add(user2);
        result.add(user3);
        return result;
    }

}
