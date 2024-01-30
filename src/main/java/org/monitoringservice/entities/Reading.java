package org.monitoringservice.entities;

import lombok.Data;
/**
 * Класс, описывающий показание счетчика.
 */
@Data
public class Reading {
    /** Поле дня. */
    private int day;
    /** Поле месяца. */
    private int month;
    /** Поле года. */
    private int year;
    /** Поле показания. */
    private int readOut;
    /**
     * Создание показания с определенными параметрами.
     * @param day день
     * @param month месяц
     * @param year год
     * @param readOut показание
     */
    public Reading(int day, int month, int year, int readOut) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.readOut = readOut;
    }
}
