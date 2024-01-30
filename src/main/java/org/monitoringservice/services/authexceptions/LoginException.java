package org.monitoringservice.services.authexceptions;

/**
 * Класс исключения. Возникает при авторизации.
 */
public class LoginException extends Exception {
    /**
     * Конструктор исключения с сообщением.
     */
    public LoginException(String message) {
        super(message);
    }
}
