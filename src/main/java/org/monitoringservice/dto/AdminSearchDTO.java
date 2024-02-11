package org.monitoringservice.dto;

import lombok.Data;

/**
 * Класс описывающий DTO. Используется во время поиска от имени администратора.
 */
@Data
public class AdminSearchDTO {
    /**
     * Поле для хранения логина пользователя.
     */
    private String login;
}
