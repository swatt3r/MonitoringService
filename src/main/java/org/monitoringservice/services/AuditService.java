package org.monitoringservice.services;

import org.monitoringservice.entities.TypeOfAction;

import java.util.LinkedList;
import java.util.List;

/**
 * Класс аудита.
 */
public class AuditService {
    /**
     * Поле для хранения действий пользователей.
     */
    private final List<String> audit;

    /**
     * Создание сервиса аудита.
     */
    public AuditService() {
        audit = new LinkedList<>();
    }

    /**
     * Метод, обеспечивающий функцию добавления нового сообщения в аудит.
     *
     * @param userLogin - логин пользователя, совершившего действие
     * @param action    - тип действия
     */
    public void saveAction(String userLogin, TypeOfAction action) {
        switch (action) {
            case Actual:
                audit.add("Пользователь " + userLogin + " получил актуальные показания.");
                break;
            case NewMeter:
                audit.add("Администратор " + userLogin + " добавил новый тип счетчика.");
                break;
            case AddMeter:
                audit.add("Пользователь " + userLogin + " зарегистрировал новый тип счетчика на себя.");
                break;
            case ShowUserMeter:
                audit.add("Пользователь " + userLogin + " получил актуальные счетчики в своем аккаунте.");
                break;
            case ShowMeters:
                audit.add("Пользователь " + userLogin + " получил актуальные счетчики.");
                break;
            case Readout:
                audit.add("Пользователь " + userLogin + " подал новое показание.");
                break;
            case History:
                audit.add("Пользователь " + userLogin + " получил историю показаний.");
                break;
            case MonthHistory:
                audit.add("Пользователь " + userLogin + " получил месячную историю показаний.");
                break;
            case Login:
                audit.add("Пользователь " + userLogin + " авторизовался.");
                break;
            case Register:
                audit.add("Пользователь " + userLogin + " зарегистрировался.");
                break;
            case LogOut:
                audit.add("Пользователь " + userLogin + " вышел из аккаунта.");
                break;
            default:
                break;
        }
    }
}
