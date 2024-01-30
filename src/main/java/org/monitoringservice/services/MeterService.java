package org.monitoringservice.services;

import org.monitoringservice.entities.Meter;
import org.monitoringservice.entities.MeterType;
import org.monitoringservice.entities.Reading;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepo;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.*;

/**
 * Класс сервиса, который работает со счетчиками и их показаниями.
 */
public class MeterService {
    /** Поле с репозиторием пользователей. */
    private final UserRepo userRepo;
    /** Поле с типами счетчиков. */
    private final ArrayList<MeterType> meterTypes;

    /**
     * Создание сервиса с определенным репозиторием пользователей.
     * @param userRepo - репозиторий пользователей
     */
    public MeterService(UserRepo userRepo) {
        this.userRepo = userRepo;
        meterTypes = new ArrayList<>();
        meterTypes.add(new MeterType("Heat"));
        meterTypes.add(new MeterType("ColdWater"));
        meterTypes.add(new MeterType("HotWater"));
    }
    /**
     * Метод добавления нового типа счетчиков.
     * @param typeName - название нового типа счетчиков
     */
    public void addNewType(String typeName) {
        meterTypes.add(new MeterType(typeName));
    }
    /**
     * Метод получения актуальных показаний счетчиков у пользователя.
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями актуальных показаний
     */
    public LinkedList<String> getUserActual(User user) {
        LinkedList<String> result = new LinkedList<>();
        for (Meter meter : user.getMeters()) {
            Reading last;
            try {
                last = meter.getHistory().getLast();
            } catch (NoSuchElementException e) {
                continue;
            }
            result.add(meter.getType().getName() + ", " + last.getDay() + "." + last.getMonth() + "." + last.getYear() + " - " + last.getReadOut());
        }
        return result;
    }
    /**
     * Метод получения актуальных показаний счетчиков у пользователей. Используется Администратором.
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @return LinkedList - список, который содержит строки с записями актуальных показаний счетчиков
     */
    public LinkedList<String> getActualForAdmin(String login) {
        LinkedList<String> adminActual = new LinkedList<>();
        for (User user : searchFromLogin(login)) {
            for (Meter meter : user.getMeters()) {
                Reading last;
                try {
                    last = meter.getHistory().getLast();
                } catch (NoSuchElementException e) {
                    continue;
                }
                adminActual.add(user.getLogin() + ", " + meter.getType().getName() + ", " + last.getDay() + "." + last.getMonth() + "." + last.getYear() + " - " + last.getReadOut());
            }
        }
        return adminActual;
    }
    /**
     * Метод получения актуальных типов счетчиков.
     * @return LinkedList - список, который содержит строки с записями актуальных счетчиков
     */
    public ArrayList<String> getMeterTypes() {
        ArrayList<String> result = new ArrayList<>(meterTypes.size());
        for (MeterType type : meterTypes) {
            result.add(type.getName());
        }
        return result;
    }
    /**
     * Метод добавления нового счетчика пользователю.
     * @param user пользователь
     * @param newType название типа счетчика, который нужно добавить
     * @throws MeterAddException если такого типа счетчика не существует или счетчик уже зарегистрирован на пользователя
     */
    public void addNewMeterToUser(User user, String newType) throws MeterAddException {
        for (MeterType type : meterTypes) {
            if (type.getName().equals(newType)) {
                for (Meter meter : user.getMeters()) {
                    if (meter.getType().getName().equals(newType)) {
                        throw new MeterAddException("Такой счетчик уже есть!");
                    }
                }
                user.getMeters().add(new Meter(new MeterType(newType)));
                return;
            }
        }
        throw new MeterAddException("Такого типа счетчика нет в системе!");
    }
    /**
     * Метод получения актуальных счетчиков пользователя.
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями актуальных счетчиков
     */
    public LinkedList<String> getUserMeters(User user) {
        LinkedList<String> result = new LinkedList<>();
        for (Meter type : user.getMeters()) {
            result.add(type.getType().getName());
        }
        return result;
    }
    /**
     * Метод получения истории показаний пользователя.
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями истории показаний
     */
    public LinkedList<String> getUserHistory(User user) {
        LinkedList<String> result = new LinkedList<>();
        for (Meter meter : user.getMeters()) {
            for (Reading read : meter.getHistory()) {
                result.add(meter.getType().getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
            }
        }
        return result;
    }
    /**
     * Метод получения истории показаний у пользователей. Используется Администратором.
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @return LinkedList - список, который содержит строки с записями истории пользователей
     */
    public LinkedList<String> getHistoryForAdmin(String login) {
        LinkedList<String> adminHistory = new LinkedList<>();
        for (User user : searchFromLogin(login)) {
            for (Meter meter : user.getMeters()) {
                for (Reading read : meter.getHistory()) {
                    adminHistory.add(user.getLogin() + ", " + meter.getType().getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
                }
            }
        }
        return adminHistory;
    }
    /**
     * Метод получения истории показаний пользователя за конкретный месяц.
     * @param user пользователь
     * @param month месяц
     * @return LinkedList - список, который содержит строки с записями истории показаний, если показания с таким месяцем не найдены, вернет пустой список
     */
    public LinkedList<String> getUserMonthHistory(User user, int month) {
        LinkedList<String> result = new LinkedList<>();
        for (Meter meter : user.getMeters()) {
            for (Reading read : meter.getHistory()) {
                if (read.getMonth() == month) {
                    result.add(meter.getType().getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
                }
            }
        }
        return result;
    }
    /**
     * Метод получения истории показаний за месяц у пользователей. Используется Администратором.
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @param month месяц
     * @return LinkedList - список, который содержит строки с записями истории пользователей, если показания с таким месяцем не найдены, вернет пустой список
     */
    public LinkedList<String> getMonthHistoryForAdmin(String login, int month) {
        LinkedList<String> adminMonth = new LinkedList<>();
        for (User user : searchFromLogin(login)) {
            for (Meter meter : user.getMeters()) {
                for (Reading read : meter.getHistory()) {
                    if (read.getMonth() == month) {
                        adminMonth.add(user.getLogin() + ", " + meter.getType().getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
                    }
                }
            }
        }
        return adminMonth;
    }
    /**
     * Метод добавления нового показания от пользователя.
     * @param user пользователь
     * @param type тип счетчика
     * @param value значение счетчика
     * @throws ReadoutException если такого типа счетчика не зарегистрировано на пользователя. Если новое показание счетчика неверно. Если запись в данном месяце уже есть
     */
    public void newReadout(User user, String type, int value) throws ReadoutException {
        for (Meter meter : user.getMeters()) {
            if (meter.getType().getName().equals(type)) {
                try {
                    Reading lastRead = meter.getHistory().getLast();
                    if (value >= lastRead.getReadOut()) {
                        Calendar rightNow = Calendar.getInstance();
                        if (rightNow.get(Calendar.MONTH) + 1 > lastRead.getMonth() || rightNow.get(Calendar.YEAR) > lastRead.getYear()) {
                            meter.getHistory().add(new Reading(rightNow.get(Calendar.DAY_OF_MONTH), rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR), value));
                        } else {
                            throw new ReadoutException("Запись в этом месяце уже есть!");
                        }
                    } else {
                        throw new ReadoutException("Неверное показание счетчика!");
                    }
                } catch (NoSuchElementException e) {
                    if (value >= 0) {
                        Calendar rightNow = Calendar.getInstance();
                        meter.getHistory().add(new Reading(rightNow.get(Calendar.DAY_OF_MONTH), rightNow.get(Calendar.MONTH) + 1, rightNow.get(Calendar.YEAR), value));
                    } else {
                        throw new ReadoutException("Неверное показание счетчика!");
                    }
                }
                return;
            }
        }
        throw new ReadoutException("Такой тип счетчика не зарегистрирован!");
    }
    /**
     * Метод получения списка пользователей по логину.
     * @param login логин пользователя
     * @return ArrayList - список, который содержит пользователей, если login "пустой", вернет всех пользователей
     */
    private ArrayList<User> searchFromLogin(String login) {
        ArrayList<User> result = new ArrayList<>();
        if (!login.isEmpty()) {
            User user = userRepo.findUserByLogin(login);
            if (user != null) {
                result.add(user);
            }
        } else {
            result = userRepo.getUsers();
        }
        return result;
    }
}