package org.monitoringservice.repositories;

import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс репозитория пользователей.
 */
@Repository
public class UserRepository implements CustomRepository {
    /**
     * Метод получения пользователя по логину.
     *
     * @param login login
     * @return User - пользователь с данным логином или null при отсутствии пользователя с таким логином
     */
    public User findUserByLogin(String login) {
        User result = null;
        String sql = "SELECT * FROM monitoring.users WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            result = getUserFromResultSet(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод получения списка всех пользователей, кроме пользователей с ролью Администратор.
     *
     * @return LinkedList - список всех пользователей
     */
    public List<User> findAllUsers() {
        List<User> result = new LinkedList<>();
        String sql = "SELECT * FROM monitoring.users WHERE role NOT IN ('ADMIN')";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result.add(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод получения пользователя по полному адресу.
     *
     * @param city            город
     * @param street          улица
     * @param houseNumber     номер дома
     * @param apartmentNumber номер квартиры
     * @return User - пользователь с данным адресом или null при отсутствии пользователя с таким адресом
     */
    public User findUserByFullAddress(String city, String street, int houseNumber, int apartmentNumber) {
        User result = null;
        String sql = "SELECT * FROM monitoring.users WHERE city = ? AND street = ? AND apartment = ? AND house = ?";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, city);
            statement.setString(2, street);
            statement.setInt(3, apartmentNumber);
            statement.setInt(4, houseNumber);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            result = getUserFromResultSet(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Метод добавления пользователя в БД.
     *
     * @param user пользователь
     */
    public void save(User user) {
        String sql = "INSERT INTO monitoring.users VALUES (DEFAULT, ?,?,?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().toString());
            statement.setString(4, user.getCity());
            statement.setString(5, user.getStreet());
            statement.setInt(6, user.getApartmentNumber());
            statement.setInt(7, user.getHouseNumber());
            statement.executeQuery();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Метод преобразования ResultSet в User. До вызова метода следует вызвать команду resultSet.next();.
     *
     * @param resultSet ResultSet из БД
     * @return User - первый пользователь в ResultSet.
     */
    private User getUserFromResultSet(ResultSet resultSet) {
        User user = null;
        try {
            user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    Role.valueOf(resultSet.getString("role")),
                    resultSet.getString("city"),
                    resultSet.getString("street"),
                    resultSet.getInt("apartment"),
                    resultSet.getInt("house"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
}