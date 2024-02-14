package org.monitoringservice.dto;

import lombok.Data;

/**
 * Класс описывающий DTO для типа счетчика.
 */
@Data
public class MeterTypeDTO {
    /**
     * Поле для хранения типа счетчика.
     */
    private String type;
}
