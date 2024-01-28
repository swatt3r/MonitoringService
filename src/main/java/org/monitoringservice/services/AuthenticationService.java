package org.monitoringservice.services;

import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepo;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
/**
 * Класс сервиса, который обрабатывает авторизацию и регистрацию.
 */
public class AuthenticationService {
    /** Поле с репозиторием пользователей. */
    private final UserRepo userRepo;
    /**
     * Создание сервиса с определенным репозиторием пользователей.
     * @param userRepo - репозиторий пользователей
     */
    public AuthenticationService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    /**
     * Метод авторизации пользователя.
     * @param login логин
     * @param password пароль
     * @return User - пользователь, который вошел в систему с заданным логином и паролем
     * @throws LoginException если неверный пароль или пользователя с таким логином не существует
     */
    public User tryToLogin(String login, String password) throws LoginException {
        User logUser = userRepo.findUserByLogin(login);
        if (logUser != null) {
            if (logUser.getPassword().equals(password)) {
                return logUser;
            } else {
                throw new LoginException("Неверный пароль!");
            }
        } else {
            throw new LoginException("Пользователь с таким именем не существует!");
        }
    }
    /**
     * Метод регистрации пользователя. При успешной регистрации ничего не возвращает.
     * @param login логин
     * @param password пароль
     * @param role роль в сервисе
     * @param city город
     * @param street улица
     * @param houseNumber номер дома
     * @param apartmentNumber номер квартиры
     * @throws RegistrationException если пользователь с таким именем уже есть или на заданной адрес уже зарегистрирован другой пользователь
     */
    public void tryToRegister(String login, String password, Role role, String city, String street,
                              int houseNumber, int apartmentNumber) throws RegistrationException {
        if (userRepo.findUserByLogin(login) == null) {
            if (userRepo.findUserByFullAddress(city, street, houseNumber, apartmentNumber) == null) {
                userRepo.save(new User(login, password, role, city, street, houseNumber, apartmentNumber));
            } else {
                throw new RegistrationException("Пользователь с данным адресом уже существует!");
            }
        } else {
            throw new RegistrationException("Пользователь с таким именем уже существует!");
        }
    }
}