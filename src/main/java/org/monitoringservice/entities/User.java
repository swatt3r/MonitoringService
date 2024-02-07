package org.monitoringservice.entities;

import lombok.Data;

/**
 * Класс, описывающий пользователя.
 */
@Data
public class User {
    /**
     * Поле уникального идентификатора.
     */
    private Integer id;
    /**
     * Поле логина.
     */
    private String login;
    /**
     * Поле пароля.
     */
    private String password;
    /**
     * Поле роли в системе.
     */
    private Role role;
    /**
     * Поле города.
     */
    private String city;
    /**
     * Поле улицы.
     */
    private String street;
    /**
     * Поле номера дома.
     */
    private int houseNumber;
    /**
     * Поле номера квартиры.
     */
    private int apartmentNumber;


    /**
     * Создание пользователя с определенными параметрами.
     *
     * @param id              уникальный идентификатор
     * @param login           логин
     * @param password        пароль
     * @param role            роль в сервисе
     * @param city            город
     * @param street          улица
     * @param houseNumber     номер дома
     * @param apartmentNumber номер квартиры
     */
    public User(int id, String login, String password, Role role, String city, String street, int houseNumber, int apartmentNumber) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartmentNumber = apartmentNumber;
    }
}