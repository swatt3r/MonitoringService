package org.monitoringservice.dto;

import lombok.Data;

/**
 * Класс описывающий DTO пользователя. Используется во время авторизации.
 */
@Data
public class UserLoginDTO {
    /**
     * Поле для хранения логина пользователя.
     */
    private String login;
    /**
     * Поле для хранения пароля пользователя.
     */
    private String password;
}
