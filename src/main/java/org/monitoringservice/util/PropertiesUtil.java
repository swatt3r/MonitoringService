package org.monitoringservice.util;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * Класс отвечающий за считывание кофигурации приложения.
 */
@Data
@Component
public class PropertiesUtil {
    /**
     * Метод получения параметров приложения.
     *
     * @return Properties - параметры приложения, которые берутся из файла application.properties
     */
    public static Map<String, Object> getApplicationProperties() {
        Yaml yaml = new Yaml();
        InputStream inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("application.yaml");
        return yaml.load(inputStream);
    }
}