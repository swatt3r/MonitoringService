package org.monitoringservice.dto;

import lombok.Data;

/**
 * Класс описывающий DTO. Используется во время поиска месячной истории.
 */
@Data
public class MonthSearchDTO {
    /**
     * Поле для хранения логина пользователя.
     */
    private String login;
    /**
     * Поле для хранения месяца.
     */
    private Integer month;
}
