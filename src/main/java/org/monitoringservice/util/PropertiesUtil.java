package org.monitoringservice.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Класс отвечающий за считывание кофигурации приложения.
 */
public class PropertiesUtil {
    public static Properties getApplicationProperties(){
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            properties.load(classLoader.getResourceAsStream("application.properties"));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return properties;
    }
}