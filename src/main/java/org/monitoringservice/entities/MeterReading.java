package org.monitoringservice.entities;

import lombok.Data;

import java.time.LocalDate;

/**
 * Класс, описывающий показание счетчика.
 */
@Data
public class MeterReading {
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
    private LocalDate date;
    /**
     * Поле показания.
     */
    private int readOut;

    /**
     * Создание показания с определенными параметрами.
     *
     * @param id      идентификатор пользователя
     * @param type    тип счетчика
     * @param date    дата
     * @param readOut показание
     */
    public MeterReading(int id, String type, LocalDate date, int readOut) {
        this.userId = id;
        this.type = type;
        this.date = date;
        this.readOut = readOut;
    }
}
