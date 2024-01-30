package org.monitoringservice.services;

import org.monitoringservice.entities.MeterType;
import org.monitoringservice.entities.Reading;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.*;

/**
 * Класс сервиса, который работает со счетчиками и их показаниями.
 */
public class MeterService {
    /**
     * Поле с репозиторием пользователей.
     */
    private final UserRepository userRepo;
    /**
     * Поле с типами счетчиков.
     */
    private final TreeMap<Integer, MeterType> meterTypes;

    /**
     * Создание сервиса с определенным репозиторием пользователей.
     *
     * @param userRepo - репозиторий пользователей
     */
    public MeterService(UserRepository userRepo) {
        this.userRepo = userRepo;
        meterTypes = new TreeMap<>();
        meterTypes.put("Heat".hashCode(), new MeterType("Heat"));
        meterTypes.put("ColdWater".hashCode(), new MeterType("ColdWater"));
        meterTypes.put("HotWater".hashCode(), new MeterType("HotWater"));
    }

    /**
     * Метод добавления нового типа счетчиков.
     *
     * @param typeName - название нового типа счетчиков
     */
    public void addNewType(String typeName) {
        if (!meterTypes.containsKey(typeName.hashCode())) {
            meterTypes.put(typeName.hashCode(), new MeterType(typeName));
        }
    }

    /**
     * Метод получения актуальных показаний счетчиков у пользователя.
     *
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями актуальных показаний
     */
    public LinkedList<String> getUserActual(User user) {
        LinkedList<String> result = new LinkedList<>();
        HashMap<MeterType, LinkedList<Reading>> history = user.getHistory();
        for (MeterType meter : history.keySet()) {
            Reading last;
            try {
                last = history.get(meter).getLast();
            } catch (NoSuchElementException e) {
                continue;
            }
            result.add(meter.getName() + ", " + last.getDay() + "." + last.getMonth() + "." + last.getYear() + " - " + last.getReadOut());
        }
        return result;
    }

    /**
     * Метод получения актуальных показаний счетчиков у пользователей. Используется Администратором.
     *
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @return LinkedList - список, который содержит строки с записями актуальных показаний счетчиков
     */
    public LinkedList<String> getActualForAdmin(String login) {
        LinkedList<String> adminActual = new LinkedList<>();
        for (User user : findByLogin(login)) {
            for (String line : getUserActual(user)) {
                adminActual.add(user.getLogin() + ", " + line);
            }
        }
        return adminActual;
    }

    /**
     * Метод получения актуальных типов счетчиков.
     *
     * @return LinkedList - список, который содержит строки с записями актуальных счетчиков
     */
    public ArrayList<String> getMeterTypes() {
        ArrayList<String> result = new ArrayList<>(meterTypes.size());
        for (MeterType type : meterTypes.values()) {
            result.add(type.getName());
        }
        return result;
    }

    /**
     * Метод добавления нового счетчика пользователю.
     *
     * @param user    пользователь
     * @param newType название типа счетчика, который нужно добавить
     * @throws MeterAddException если такого типа счетчика не существует или счетчик уже зарегистрирован на пользователя
     */
    public void addNewMeterToUser(User user, String newType) throws MeterAddException {
        if (meterTypes.containsKey(newType.hashCode())) {
            if (user.getHistory().containsKey(new MeterType(newType))) {
                throw new MeterAddException("Такой счетчик уже есть!");
            }
            user.getHistory().put(new MeterType(newType), new LinkedList<>());
            return;
        }
        throw new MeterAddException("Такого типа счетчика нет в системе!");
    }

    /**
     * Метод получения актуальных счетчиков пользователя.
     *
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями актуальных счетчиков
     */
    public LinkedList<String> getUserMeters(User user) {
        LinkedList<String> result = new LinkedList<>();
        for (MeterType type : user.getHistory().keySet()) {
            result.add(type.getName());
        }
        return result;
    }

    /**
     * Метод получения истории показаний пользователя.
     *
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями истории показаний
     */
    public LinkedList<String> getUserHistory(User user) {
        LinkedList<String> result = new LinkedList<>();
        for (MeterType type : user.getHistory().keySet()) {
            for (Reading read : user.getHistory().get(type)) {
                result.add(type.getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
            }
        }
        return result;
    }

    /**
     * Метод получения истории показаний у пользователей. Используется Администратором.
     *
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @return LinkedList - список, который содержит строки с записями истории пользователей
     */
    public LinkedList<String> getHistoryForAdmin(String login) {
        LinkedList<String> adminHistory = new LinkedList<>();
        for (User user : findByLogin(login)) {
            for (String line : getUserHistory(user)) {
                adminHistory.add(user.getLogin() + ", " + line);
            }
        }
        return adminHistory;
    }

    /**
     * Метод получения истории показаний пользователя за конкретный месяц.
     *
     * @param user  пользователь
     * @param month месяц
     * @return LinkedList - список, который содержит строки с записями истории показаний, если показания с таким месяцем не найдены, вернет пустой список
     */
    public LinkedList<String> getUserMonthHistory(User user, int month) {
        LinkedList<String> result = new LinkedList<>();
        for (MeterType type : user.getHistory().keySet()) {
            for (Reading read : user.getHistory().get(type)) {
                if (read.getMonth() == month) {
                    result.add(type.getName() + ", " + read.getDay() + "." + read.getMonth() + "." + read.getYear() + " - " + read.getReadOut());
                }
            }
        }
        return result;
    }

    /**
     * Метод получения истории показаний за месяц у пользователей. Используется Администратором.
     *
     * @param login логин пользователя, если строка пуста, то поиск будет по всем пользователям
     * @param month месяц
     * @return LinkedList - список, который содержит строки с записями истории пользователей, если показания с таким месяцем не найдены, вернет пустой список
     */
    public LinkedList<String> getMonthHistoryForAdmin(String login, int month) {
        LinkedList<String> adminMonth = new LinkedList<>();
        for (User user : findByLogin(login)) {
            for (String line : getUserMonthHistory(user, month)) {
                adminMonth.add(user.getLogin() + ", " + line);
            }
        }
        return adminMonth;
    }

    /**
     * Метод добавления нового показания от пользователя.
     *
     * @param user  пользователь
     * @param type  тип счетчика
     * @param value значение счетчика
     * @throws ReadoutException если такого типа счетчика не зарегистрировано на пользователя. Если новое показание счетчика неверно. Если запись в данном месяце уже есть
     */
    public void newReadout(User user, String type, int value) throws ReadoutException {
        if (meterTypes.containsKey(type.hashCode())) {
            LinkedList<Reading> history = user.getHistory().get(new MeterType(type));
            try {
                Reading lastRead = history.getLast();

                if (value <= lastRead.getReadOut()) {
                    throw new ReadoutException("Неверное показание счетчика!");
                }

                int[] date = getCurrentDate();
                if (date[1] + 1 > lastRead.getMonth() || date[2] > lastRead.getYear()) {
                    history.add(new Reading(date[0], date[1] + 1, date[2], value));
                } else {
                    throw new ReadoutException("Запись в этом месяце уже есть!");
                }

            } catch (NoSuchElementException e) {
                int[] date = getCurrentDate();
                history.add(new Reading(date[0], date[1] + 1, date[2], value));
            }
            return;
        }
        throw new ReadoutException("Такой тип счетчика не зарегистрирован!");
    }

    /**
     * Метод получения текущей даты.
     *
     * @return int[] - массив, состоящий из дня, месяца и года.
     */
    private int[] getCurrentDate() {
        Calendar rightNow = Calendar.getInstance();
        return new int[]{rightNow.get(Calendar.DAY_OF_MONTH), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.YEAR)};
    }

    /**
     * Метод получения списка пользователей по логину.
     *
     * @param login логин пользователя
     * @return ArrayList - список, который содержит пользователей, если login "пустой", вернет всех пользователей
     */
    private ArrayList<User> findByLogin(String login) {
        ArrayList<User> result = new ArrayList<>();
        if (!login.isEmpty()) {
            User user = userRepo.findUserByLogin(login);
            if (user != null) {
                result.add(user);
            }
        } else {
            result = new ArrayList<>(userRepo.getUsers().values());
        }
        return result;
    }
}