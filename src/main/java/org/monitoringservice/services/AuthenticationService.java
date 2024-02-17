package org.monitoringservice.services;

import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.util.annotations.Audit;
import org.monitoringservice.util.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Класс сервиса, который обрабатывает авторизацию и регистрацию.
 */
@Service
public class AuthenticationService {
    /**
     * Поле с репозиторием пользователей.
     */
    private final UserRepository userRepo;

    /**
     * Создание сервиса с определенным репозиторием пользователей.
     *
     * @param userRepo - репозиторий пользователей
     */
    @Autowired
    public AuthenticationService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Метод авторизации пользователя.
     *
     * @param login    логин
     * @param password пароль
     * @return User - пользователь, который вошел в систему с заданным логином и паролем
     * @throws LoginException если неверный пароль или пользователя с таким логином не существует
     */
    @Audit(typeOfAction = TypeOfAction.Login, haveLogin = true, identifierPos = 0)
    public UserDTO login(String login, String password) throws LoginException {
        User logUser = userRepo.findUserByLogin(login);
        if (logUser == null) {
            throw new LoginException("Пользователь с таким именем не существует!");
        }
        if (logUser.getPassword().equals(password)) {
            return UserMapper.MAPPER.userToUserDTO(logUser);
        } else {
            throw new LoginException("Неверный пароль!");
        }
    }

    /**
     * Метод регистрации пользователя. При успешной регистрации ничего не возвращает.
     *
     * @param login           логин
     * @param password        пароль
     * @param role            роль в сервисе
     * @param city            город
     * @param street          улица
     * @param houseNumber     номер дома
     * @param apartmentNumber номер квартиры
     * @throws RegistrationException если пользователь с таким именем уже есть или на заданной адрес уже зарегистрирован другой пользователь
     */
    @Audit(typeOfAction = TypeOfAction.Register, haveLogin = true, identifierPos = 0)
    public void addNewUser(String login, String password, Role role, String city, String street,
                              int houseNumber, int apartmentNumber) throws RegistrationException {
        User regUser = userRepo.findUserByLogin(login);
        if (regUser != null) {
            throw new RegistrationException("Пользователь с таким именем уже существует!");
        }

        regUser = userRepo.findUserByFullAddress(city, street, houseNumber, apartmentNumber);
        if (regUser == null) {
            userRepo.save(new User(-1, login, password, role, city, street, houseNumber, apartmentNumber));
        } else {
            throw new RegistrationException("Пользователь с данным адресом уже существует!");
        }
    }
}