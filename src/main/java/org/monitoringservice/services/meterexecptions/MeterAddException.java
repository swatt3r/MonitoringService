package org.monitoringservice.services.meterexecptions;
/**
 * Класс исключения. Возникает при добавлении нового счетчика.
 */
public class MeterAddException extends Exception {
    /**
     * Конструктор исключения с сообщением.
     */
    public MeterAddException(String message) {
        super(message);
    }
}
