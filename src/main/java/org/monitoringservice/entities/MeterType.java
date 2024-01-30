package org.monitoringservice.entities;

import lombok.Data;

/**
 * Класс, описывающий тип счетчика.
 */
@Data
public class MeterType {
    /**
     * Поле имени(названия) счетчика.
     */
    private String name;

    /**
     * Создание счетчика с определенными параметрами.
     *
     * @param name имя(название)
     */
    public MeterType(String name) {
        this.name = name;
    }
}
