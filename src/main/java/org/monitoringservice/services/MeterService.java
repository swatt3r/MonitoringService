package org.monitoringservice.services;

import org.monitoringservice.entities.Reading;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Класс сервиса, который работает со счетчиками и их показаниями.
 */
public class MeterService {
    /**
     * Поле с репозиторием истории показаний и счетчиков.
     */
    private final MeterRepository meterRepository;
    /**
     * Поле с репозиторием пользователей.
     */
    private final UserRepository userRepo;


    /**
     * Создание сервиса с определенным репозиторием пользователей.
     *
     * @param userRepo    - репозиторий пользователей
     * @param historyRepo - репозиторий истории показаний и счетчиков
     */
    public MeterService(UserRepository userRepo, MeterRepository historyRepo) {
        this.meterRepository = historyRepo;
        this.userRepo = userRepo;
    }

    /**
     * Метод добавления нового типа счетчиков.
     *
     * @param typeName - название нового типа счетчиков
     */
    public void addNewType(String typeName) {
        LinkedList<String> types = meterRepository.findAllMetersTypes();
        if (!types.contains(typeName)) {
            meterRepository.addNewType(typeName);
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
        LinkedList<Reading> readings = meterRepository.findUserActualHistory(user.getId());
        for (Reading read : readings) {
            result.add(read.getType() + ", " + read.getDate() + " - " + read.getReadOut());
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
    public LinkedList<String> getMeterTypes() {
        return meterRepository.findAllMetersTypes();
    }

    /**
     * Метод добавления нового счетчика пользователю.
     *
     * @param user    пользователь
     * @param newType название типа счетчика, который нужно добавить
     * @throws MeterAddException если такого типа счетчика не существует или счетчик уже зарегистрирован на пользователя
     */
    public void addNewMeterToUser(User user, String newType) throws MeterAddException {
        LinkedList<String> types = meterRepository.findAllMetersTypes();
        if (!types.contains(newType)) {
            throw new MeterAddException("Такого типа счетчика нет в системе!");
        }

        types = meterRepository.findMetersByUserId(user.getId());
        if (!types.contains(newType)) {
            meterRepository.addNewTypeToUser(user.getId(), newType);
        } else {
            throw new MeterAddException("Такой счетчик уже есть!");
        }
    }

    /**
     * Метод получения актуальных счетчиков пользователя.
     *
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями актуальных счетчиков
     */
    public LinkedList<String> getUserMeters(User user) {
        return meterRepository.findMetersByUserId(user.getId());
    }

    /**
     * Метод получения истории показаний пользователя.
     *
     * @param user пользователь
     * @return LinkedList - список, который содержит строки с записями истории показаний
     */
    public LinkedList<String> getUserHistory(User user) {
        LinkedList<String> result = new LinkedList<>();
        LinkedList<Reading> readings = meterRepository.findHistoryByUserId(user.getId());
        for (Reading read : readings) {
            result.add(read.getType() + ", " + read.getDate() + " - " + read.getReadOut());
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
        LinkedList<Reading> readings = meterRepository.findMonthHistoryByUserId(user.getId(), month);
        for (Reading read : readings) {
            result.add(read.getType() + ", " + read.getDate() + " - " + read.getReadOut());
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
        LinkedList<String> types = meterRepository.findMetersByUserId(user.getId());
        if (!types.contains(type)) {
            throw new ReadoutException("Такой тип счетчика не зарегистрирован!");
        }

        if (value < 0) {
            throw new ReadoutException("Неверное показание счетчика!");
        }

        LinkedList<Reading> userActual = meterRepository.findUserActualHistory(user.getId());
        Reading last = null;
        for (Reading reading : userActual) {
            if (reading.getType().equals(type)) {
                last = reading;
                break;
            }
        }
        if (last != null) {
            if (last.getReadOut() > value) {
                throw new ReadoutException("Неверное показание счетчика!");
            }

            int[] currentDate = getCurrentDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(last.getDate());
            if (currentDate[1] > calendar.get(Calendar.MONTH) || currentDate[2] > calendar.get(Calendar.YEAR)) {
                meterRepository.addNewReadout(user.getId(), type, new Date(), value);
            } else {
                throw new ReadoutException("Запись в этом месяце уже есть!");
            }
        } else {
            meterRepository.addNewReadout(user.getId(), type, new Date(), value);
        }
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
    private LinkedList<User> findByLogin(String login) {
        LinkedList<User> result = new LinkedList<>();
        if (!login.isEmpty()) {
            User user = userRepo.findUserByLogin(login);
            if (user != null) {
                result.add(user);
            }
        } else {
            result = userRepo.findAllUsers();
        }
        return result;
    }
}
