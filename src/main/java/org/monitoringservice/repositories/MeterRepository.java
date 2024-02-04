package org.monitoringservice.repositories;

import org.monitoringservice.entities.Reading;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Класс репозитория истории показаний и счетчиков.
 */
public class MeterRepository {
    /**
     * Поле URL для подключения с БД.
     */
    private final String URL;
    /**
     * Поле USERNAME для подключения с БД.
     */
    private final String USERNAME;
    /**
     * Поле PASSWORD для подключения с БД.
     */
    private final String PASSWORD;

    /**
     * Задание параметров, нужных для подключения к базе данных.
     */
    public MeterRepository(Properties properties) {
        URL = properties.getProperty("url");
        USERNAME = properties.getProperty("username");
        PASSWORD = properties.getProperty("password");
    }

    /**
     * Метод получения истории показаний конкретного пользователя из БД.
     *
     * @param userId идентификатор пользователя
     * @return LinkedList - Список в котором содержится вся история показаний пользователя с даныым id.
     */
    public LinkedList<Reading> findHistoryByUserId(int userId) {
        LinkedList<Reading> result = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            result = getReadingsFromResultSet(resultSet);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод получения зарегистрированных счетчиков на конкретного пользователя из БД.
     *
     * @param userId идентификатор пользователя
     * @return LinkedList - Список в котором содержатся все счетчики, которые есть у пользователя с даныым id.
     */
    public LinkedList<String> findMetersByUserId(int userId) {
        LinkedList<String> result = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            String sql = "SELECT type FROM monitoring.users_meters WHERE user_id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString("type"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод получения истории показаний за месяц конкретного пользователя из БД.
     *
     * @param userId идентификатор пользователя
     * @param month  месяц
     * @return LinkedList - Список в котором содержится вся история показаний за месяц пользователя с даныым id.
     */
    public LinkedList<Reading> findMonthHistoryByUserId(int userId, int month) {
        LinkedList<Reading> result = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ? AND to_char(date , 'MM') = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            if (month < 10) {
                statement.setString(2, "0" + month);
            } else {
                statement.setString(2, String.valueOf(month));
            }


            ResultSet resultSet = statement.executeQuery();
            result = getReadingsFromResultSet(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод получения всех возможных типов счетчиков из БД.
     *
     * @return LinkedList - Список в котором содержатся все доступные типы счетчиков.
     */
    public LinkedList<String> findAllMetersTypes() {
        LinkedList<String> result = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            String sql = "SELECT * FROM monitoring.meter_types";

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result.add(resultSet.getString("type"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод добавления нового типа счетчика в БД.
     *
     * @param newType название нового типа счетчика
     */
    public void addNewType(String newType) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO monitoring.meter_types VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, newType);
            statement.executeQuery();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Метод регистрации нового типа счетчика на пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param newType название нового типа счетчика
     */
    public void addNewTypeToUser(int userId, String newType) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO monitoring.users_meters VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            statement.setString(2, newType);
            statement.executeQuery();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Метод получения актуальных показаний конкретного пользователя из БД.
     *
     * @param userId идентификатор пользователя
     * @return LinkedList - Список в котором содержатся актуальные показания пользователя с даныым id.
     */
    public LinkedList<Reading> findUserActualHistory(int userId) {
        LinkedList<Reading> result = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            LinkedList<String> userTypes = findMetersByUserId(userId);
            String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ? AND type = ? AND date = (SELECT MAX(date) FROM monitoring.meter_history WHERE type = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            for (String type : userTypes) {
                statement.setInt(1, userId);
                statement.setString(2, type);
                statement.setString(3, type);

                ResultSet resultSet = statement.executeQuery();
                result.addAll(getReadingsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод добавления нового показания в БД.
     *
     * @param userId идентификатор пользователя
     * @param type   тип счетчика
     * @param date   дата подачи показания
     * @param value  значение счетчика
     */
    public void addNewReadout(int userId, String type, Date date, int value) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO monitoring.meter_history VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            statement.setString(2, type);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            statement.setInt(4, value);
            statement.executeQuery();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Метод преобразования ResultSet в LinkedList&lt;Reading&gt;.
     *
     * @param resultSet ResultSet из БД
     * @return LinkedList - Список в котором содержатся показания, находящиеся в ResultSet.
     */
    private LinkedList<Reading> getReadingsFromResultSet(ResultSet resultSet) {
        LinkedList<Reading> result = new LinkedList<>();
        try {
            while (resultSet.next()) {
                result.add(new Reading(
                        resultSet.getInt("user_id"),
                        resultSet.getString("type"),
                        resultSet.getDate("date"),
                        resultSet.getInt("value")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}