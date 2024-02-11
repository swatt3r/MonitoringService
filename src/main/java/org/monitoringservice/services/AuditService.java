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
    private static final List<String> audit = new LinkedList<>();

    /**
     * Метод, обеспечивающий функцию добавления нового сообщения в аудит.
     *
     * @param userLogin логин пользователя, совершившего действие
     * @param action    тип действия
     */
    public static void saveAction(String userLogin, int id, TypeOfAction action) {
        switch (action) {
            case Actual:
                if (id > 0) {
                    audit.add("Пользователь " + id + " получил актуальные показания.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " получил актуальные показания.");
                break;
            case NewMeter:
                audit.add("Администратор добавил новый тип счетчика.");
                break;
            case AddMeter:
                if (id > 0) {
                    audit.add("Пользователь " + id + " зарегистрировал новый тип счетчика на себя.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " зарегистрировал новый тип счетчика на себя.");
                break;
            case ShowUserMeter:
                if (id > 0) {
                    audit.add("Пользователь " + id + " получил актуальные счетчики в своем аккаунте.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " получил актуальные счетчики в своем аккаунте.");
                break;
            case Readout:
                if (id > 0) {
                    audit.add("Пользователь " + id + " подал новое показание.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " подал новое показание.");
                break;
            case History:
                if (id > 0) {
                    audit.add("Пользователь " + id + " получил историю показаний.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " получил историю показаний.");
                break;
            case MonthHistory:
                if (id > 0) {
                    audit.add("Пользователь " + id + " получил месячную историю показаний.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " получил месячную историю показаний.");
                break;
            case Login:
                if (id > 0) {
                    audit.add("Пользователь " + id + " авторизовался.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " авторизовался.");
                break;
            case Register:
                if (id > 0) {
                    audit.add("Пользователь " + id + " зарегистрировался.");
                    break;
                }
                audit.add("Пользователь " + userLogin + " зарегистрировался.");
                break;
            default:
                break;
        }
    }
}
