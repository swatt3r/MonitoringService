package org.monitoringservice.entities;

import lombok.Data;

import java.util.Date;

/**
 * Класс, описывающий показание счетчика.
 */
@Data
public class Reading {
    /**
     * Поле уникального идентификатора пользователя.
     */
    private int userId;
    /**
     * Поле типа счетчика.
     */
    private String type;
    /**
     * Поле даты.
     */
    private Date date;
    /**
     * Поле показания.
     */
    private int readOut;

    /**
     * Создание показания с определенными параметрами.
     *
     * @param id идентификатор пользователя
     * @param type тип счетчика
     * @param date дата
     * @param readOut показание
     */
    public Reading(int id, String type,Date date, int readOut) {
        this.userId = id;
        this.type = type;
        this.date = date;
        this.readOut = readOut;
    }
}
