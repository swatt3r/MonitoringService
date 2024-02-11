package org.monitoringservice.dto;

import lombok.Data;
import org.monitoringservice.entities.Role;

/**
 * Класс описывающий DTO пользователя.
 */
@Data
public class UserDTO {
    /**
     * Поле для хранения логина пользователя.
     */
    private String login;
    /**
     * Поле для хранения пароля пользователя.
     */
    private String password;
    /**
     * Поле для хранения роли пользователя.
     */
    private Role role;
    /**
     * Поле для хранения идентификатора пользователя.
     */
    private Integer id;
    /**
     * Поле для хранения города пользователя.
     */
    private String city;
    /**
     * Поле для хранения улицы пользователя.
     */
    private String street;
    /**
     * Поле для хранения номера дома пользователя.
     */
    private Integer houseNumber;
    /**
     * Поле для хранения номера квартиры пользователя.
     */
    private Integer apartmentNumber;
}