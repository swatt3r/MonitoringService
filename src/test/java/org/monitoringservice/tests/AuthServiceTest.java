package org.monitoringservice.tests;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Класс тестов для сервиса, который обрабатывает авторизацию и регистрацию.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    /**
     * Поле с сервисом.
     */
    @InjectMocks
    private AuthenticationService authService;
    /**
     * Mock репозитория.
     */
    @Mock
    private UserRepository userRepo;

    @Test
    @DisplayName("Тест авторизации с неправильным паролем.")
    void tryToLoginTest_wrongPassword() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("admin")))
                .thenReturn(new User("admin", "admin", Role.ADMIN, "", "", -1, -1));
        assertThatThrownBy(() ->
                authService.login("admin", "user"))
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Неверный пароль!");
    }

    @Test
    @DisplayName("Тест авторизации с неправильным логином.")
    void tryToLoginTest_wrongLogin() {
        Mockito.when(userRepo.findUserByLogin(Mockito.argThat(arg -> !arg.isEmpty() && !arg.equals("admin")))).thenReturn(null);
        assertThatThrownBy(() ->
                authService.login("noUser", "admin"))
                .isInstanceOf(LoginException.class)
                .hasMessageContaining("Пользователь с таким именем не существует!");
    }

    @Test
    @DisplayName("Тест успешной авторизации.")
    void tryToLoginTest_success() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("admin")))
                .thenReturn(new User("admin", "admin", Role.ADMIN, "", "", -1, -1));
        try {
            assertThat(authService.login("admin", "admin")).isEqualTo(userRepo.findUserByLogin("admin"));
        } catch (LoginException e) {
            Assertions.fail("Неправильная авторизация");
        }
    }

    @Test
    @DisplayName("Тест регистрации с уже существующим логином.")
    void tryToRegisterTest_sameLogin() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("user")))
                .thenReturn(new User("user", "user", Role.USER, "city", "str", 11, 12));

        assertThatThrownBy(() ->
                authService.register("user", "user", Role.USER, "city", "str", 11, 12))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Пользователь с таким именем уже существует!");
    }

    @Test
    @DisplayName("Тест регистрации с уже существующим адресом.")
    void tryToRegisterTest_sameAddress() {
        Mockito.when(userRepo.findUserByFullAddress("city", "str", 11, 12))
                .thenReturn(new User("user", "user", Role.USER, "city", "str", 11, 12));
        assertThatThrownBy(() ->
                authService.register("newUser", "user", Role.USER, "city", "str", 11, 12))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Пользователь с данным адресом уже существует!");
    }

    @Test
    @DisplayName("Тест успешной регистрации.")
    void tryToRegisterTest_success() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(null);
        Mockito.when(userRepo.findUserByFullAddress("city", "str", 11, 12))
                .thenReturn(null);
        try {
            authService.register("newUser", "user", Role.USER, "city", "str", 11, 12);
        } catch (RegistrationException e) {
            Assertions.fail("Неправильная регистрация");
        }
    }
}
