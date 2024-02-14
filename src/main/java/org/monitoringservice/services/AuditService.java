package org.monitoringservice.services;

import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.repositories.UtilRepository;

/**
 * Класс аудита.
 */
public class AuditService {
    /**
     * Метод, обеспечивающий функцию добавления нового сообщения в аудит.
     *
     * @param userLogin логин пользователя, совершившего действие
     * @param action    тип действия
     */
    public static void saveAction(String userLogin, int id, TypeOfAction action) {
        StringBuilder result = new StringBuilder();
        if(id > 0){
            result.append("Пользователь ").append(id).append(" ");
        }
        else {
            result.append("Пользователь ").append(userLogin).append(" ");
        }
        switch (action) {
            case Actual:
                result.append("получил актуальные показания.");
                break;
            case NewMeter:
                result.setLength(0);
                result.append("Администратор добавил новый тип счетчика.");
                break;
            case AddMeter:
                result.append("зарегистрировал новый тип счетчика на себя.");
                break;
            case ShowUserMeter:
                result.append("получил актуальные счетчики в своем аккаунте.");
                break;
            case Readout:
                result.append("подал новое показание.");
                break;
            case History:
                result.append("получил историю показаний.");
                break;
            case MonthHistory:
                result.append("получил месячную историю показаний.");
                break;
            case Login:
                result.append("авторизовался.");
                break;
            case Register:
                result.append("зарегистрировался.");
                break;
            default:
                break;
        }
        UtilRepository.insertAudit(result.toString());
    }
}