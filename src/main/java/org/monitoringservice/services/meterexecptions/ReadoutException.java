package org.monitoringservice.services.meterexecptions;

/**
 * Класс исключения. Возникает при добавлении нового показания.
 */
public class ReadoutException extends Exception {
    /**
     * Конструктор исключения с сообщением.
     */
    public ReadoutException(String message) {
        super(message);
    }
}
