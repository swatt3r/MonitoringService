package org.monitoringservice.entities;

import lombok.Data;

import java.util.LinkedList;
/**
 * Класс, описывающий счетик.
 */
@Data
public class Meter {
    /** Поле типа счетчика. */
    private MeterType type;
    /** Поле истории показаний. */
    private LinkedList<Reading> history;
    /**
     * Создание счетчика с определенными параметрами.
     * @param type тип счетчика
     */
    public Meter(MeterType type) {
        this.type = type;
        history = new LinkedList<>();
    }
}
