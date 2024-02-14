package org.monitoringservice.in.servlets.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.util.MigrationUtil;
import org.monitoringservice.util.PropertiesUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Properties;

/**
 * Класс ContextListener. Используется для инициализации контекста сервлетов.
 */
@WebListener
public class ContextListener implements ServletContextListener {
    /**
     * Метод инициализации контекста.
     *
     * @param servletContextEvent событие контекста
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Properties properties = PropertiesUtil.getApplicationProperties();
        MigrationUtil.migrateDB(properties);

        UserRepository userRepository = new UserRepository();
        MeterRepository meterRepository = new MeterRepository();

        AuthenticationService authenticationService = new AuthenticationService(userRepository);
        MeterService meterService = new MeterService(userRepository, meterRepository);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("authService", authenticationService);
        context.setAttribute("meterService", meterService);
        context.setAttribute("mapper", objectMapper);
    }
    /**
     * Метод разрушения контекста.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}