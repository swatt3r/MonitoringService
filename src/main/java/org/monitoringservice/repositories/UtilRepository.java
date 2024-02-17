package org.monitoringservice.repositories;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс репозитория служебных сообщений.
 */
@Repository
public class UtilRepository implements CustomRepository {
    /**
     * Метод добавления аудита в БД.
     *
     * @param message сообщение
     */
    public static void insertAudit(String message) {
        String sql = "INSERT INTO util.audit VALUES (?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, message);
            statement.executeQuery();
        } catch (SQLException ignored) {
        }
    }
}
