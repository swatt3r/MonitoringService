package org.monitoringservice.repositories;

import org.monitoringservice.util.PropertiesUtil;

/**
 * Интерфейс репозитория.
 */
public interface CustomRepository {
    /**
     * Поле URL для подключения с БД.
     */
    String URL = (String) PropertiesUtil.getApplicationProperties().get("url");
    /**
     * Поле USERNAME для подключения с БД.
     */
    String USERNAME = (String) PropertiesUtil.getApplicationProperties().get("username");
    /**
     * Поле PASSWORD для подключения с БД.
     */
    String PASSWORD = (String) PropertiesUtil.getApplicationProperties().get("password");
}
