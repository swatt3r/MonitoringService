package org.monitoringservice.repositories;

import lombok.Getter;
import org.monitoringservice.entities.*;

import java.util.ArrayList;

/**
 * Класс репозитория пользователей.
 */
@Getter
public class UserRepo {
    /** Поле со списком пользователей. */
    private final ArrayList<User> users;
    /**
     * Создание репозитория без пользователей, но с администратором.
     */
    public UserRepo() {
        users = new ArrayList<>();
        users.add(new User("admin", "admin", Role.ADMIN,
                "", "", -1, -1));
    }
    /**
     * Метод получения пользователя по логину.
     * @param login login
     * @return User - пользователь с данным логином или null при отсутствии пользователя с таким логином
     */
    public User findUserByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }
    /**
     * Метод получения пользователя по полному адресу.
     * @param city город
     * @param street улица
     * @param houseNumber номер дома
     * @param apartmentNumber номер квартиры
     * @return User - пользователь с данным адресом или null при отсутствии пользователя с таким адресом
     */
    public User findUserByFullAddress(String city, String street, int houseNumber, int apartmentNumber) {
        for (User user : users) {
            if (user.getCity().equals(city) && user.getStreet().equals(street) && user.getHouseNumber() == houseNumber && user.getApartmentNumber() == apartmentNumber) {
                return user;
            }
        }
        return null;
    }
    /**
     * Метод добавления пользователя в репозиторий.
     * @param user пользователь
     */
    public void save(User user) {
        users.add(user);
    }
}