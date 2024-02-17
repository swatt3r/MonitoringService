package org.monitoringservice.services;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Класс сервиса логгирования.
 */
@Service
public class LogService {
    /**
     * Поле для хранения логов.
     */
    public static final List<String> log = new LinkedList<>();
    /**
     * Метод, обеспечивающий функцию добавления нового лога.
     *
     * @param logMessage сообщение для логгирования
     */
    public static void log(String logMessage) {
        log.add(logMessage);
    }
}
