package org.monitoringservice.dto;

import lombok.Data;

/**
 * Класс описывающий DTO для подачи нового показания.
 */
@Data
public class NewReadoutDTO {
    /**
     * Поле для хранения типа счетчика.
     */
    private String type;
    /**
     * Поле для хранения показания.
     */
    private int readOut;
}
