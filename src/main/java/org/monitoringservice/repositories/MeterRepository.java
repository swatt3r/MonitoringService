package org.monitoringservice.repositories;

import org.monitoringservice.entities.MeterReading;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс репозитория истории показаний и счетчиков.
 */
public class MeterRepository implements Repository {
    /**
     * Метод получения истории показаний конкретного пользователя из БД.
     *
     * @param userId идентификатор пользователя
     * @return LinkedList - Список в котором содержится вся история показаний пользователя с даныым id.
     */
    public List<MeterReading> findHistoryByUserId(int userId) {
        List<MeterReading> result = new LinkedList<>();
        String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
    public List<String> findMetersByUserId(int userId) {
        List<String> result = new LinkedList<>();
        String sql = "SELECT type FROM monitoring.users_meters WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
    public List<MeterReading> findMonthHistoryByUserId(int userId, int month) {
        List<MeterReading> result = new LinkedList<>();
        String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ? AND to_char(date , 'MM') = ?;";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
    public List<String> findAllMetersTypes() {
        List<String> result = new LinkedList<>();
        String sql = "SELECT * FROM monitoring.meter_types";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {

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
        String sql = "INSERT INTO monitoring.meter_types VALUES (?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
        String sql = "INSERT INTO monitoring.users_meters VALUES (?,?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

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
    public List<MeterReading> findUserActualHistory(int userId) {
        List<MeterReading> result = new LinkedList<>();
        String sql = "SELECT * FROM monitoring.meter_history WHERE user_id = ? AND type = ? AND date = (SELECT MAX(date) FROM monitoring.meter_history WHERE type = ?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            List<String> userTypes = findMetersByUserId(userId);

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
    public void addNewReadout(int userId, String type, LocalDate date, int value) {
        String sql = "INSERT INTO monitoring.meter_history VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, type);
            statement.setDate(3, java.sql.Date.valueOf(date));
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
    private List<MeterReading> getReadingsFromResultSet(ResultSet resultSet) {
        List<MeterReading> result = new LinkedList<>();
        try {
            while (resultSet.next()) {
                result.add(new MeterReading(
                        resultSet.getInt("user_id"),
                        resultSet.getString("type"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getInt("value")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}