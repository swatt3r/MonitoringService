package org.monitoringservice.repositories;

import org.monitoringservice.util.PropertiesUtil;

/**
 * Интерфейс репозитория.
 */
public interface Repository {
    /**
     * Поле URL для подключения с БД.
     */
    String URL = PropertiesUtil.getApplicationProperties().getProperty("url");
    /**
     * Поле USERNAME для подключения с БД.
     */
    String USERNAME = PropertiesUtil.getApplicationProperties().getProperty("username");
    /**
     * Поле PASSWORD для подключения с БД.
     */
    String PASSWORD = PropertiesUtil.getApplicationProperties().getProperty("password");
}
