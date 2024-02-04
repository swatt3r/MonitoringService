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
import java.util.Properties;

/**
 * Класс отвечающий за миграцию БД.
 */
public class MigrationUtil {

    /**
     * Метод производящий миграции БД.
     *
     * @param properties параметры приложения, в которых должна быть информация о подключении к БД и дериктории, где находится changeLogFile
     */
    public static void migrateDB(Properties properties) {
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"))) {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase(properties.getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
    }
}
