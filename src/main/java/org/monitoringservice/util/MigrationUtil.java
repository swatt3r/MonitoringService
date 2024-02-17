package org.monitoringservice.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Класс отвечающий за миграцию БД.
 */
public class MigrationUtil {

    /**
     * Метод производящий миграции БД.
     *
     * @param properties параметры приложения, в которых должна быть информация о подключении к БД и дериктории, где находится changeLogFile
     */
    public static void migrateDB(Map<String, Object> properties) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try (Connection connection = DriverManager.getConnection(
                (String) properties.get("url"),
                (String) properties.get("username"),
                (String) properties.get("password"))) {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase((String) properties.get("changeLogFile"), new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
    }
}
