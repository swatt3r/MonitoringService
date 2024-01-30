package org.monitoringservice.services.authexceptions;

/**
 * Класс исключения. Возникает при регистрации.
 */
public class RegistrationException extends Exception {
    /**
     * Конструктор исключения с сообщением.
     */
    public RegistrationException(String message) {
        super(message);
    }
}
