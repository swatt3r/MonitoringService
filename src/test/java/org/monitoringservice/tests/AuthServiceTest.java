package org.monitoringservice.tests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepo;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
/**
 * Класс тестов для сервиса, который обрабатывает авторизацию и регистрацию.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    /** Поле с сервисом. */
    @InjectMocks
    private AuthenticationService authService;
    /** Mock репозитория. */
    @Mock
    private UserRepo userRepo;
    /**
     * Тест авторизации с неправильным паролем.
     */
    @Test
    void tryToLoginTest_wrongPassword() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("admin")))
                .thenReturn(new User("admin", "admin", Role.ADMIN, "", "", -1, -1));
        LoginException thrown = Assertions.assertThrows(LoginException.class, () -> authService.tryToLogin("admin", "user"));
        Assertions.assertEquals("Неверный пароль!", thrown.getMessage());
    }
    /**
     * Тест авторизации с неправильным логином.
     */
    @Test
    void tryToLoginTest_wrongLogin() {
        Mockito.when(userRepo.findUserByLogin(Mockito.argThat(arg -> !arg.isEmpty() && !arg.equals("admin")))).thenReturn(null);
        LoginException thrown = Assertions.assertThrows(LoginException.class, () -> authService.tryToLogin("noUser", "admin"));
        Assertions.assertEquals("Пользователь с таким именем не существует!", thrown.getMessage());
    }
    /**
     * Тест успешной авторизации.
     */
    @Test
    void tryToLoginTest_success() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("admin")))
                .thenReturn(new User("admin", "admin", Role.ADMIN, "", "", -1, -1));
        try {
            Assertions.assertEquals(new User("admin", "admin", Role.ADMIN, "", "", -1, -1),
                    authService.tryToLogin("admin", "admin"));
        } catch (LoginException e) {
            Assertions.fail();
        }
    }
    /**
     * Тест регистрации с уже существующим логином.
     */
    @Test
    void tryToRegisterTest_sameLogin() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("user")))
                .thenReturn(new User("user", "user", Role.USER, "city", "str", 11, 12));
        RegistrationException thrown = Assertions.assertThrows(
                RegistrationException.class,
                () -> authService.tryToRegister("user", "user", Role.USER, "city", "str", 11, 12));
        Assertions.assertEquals("Пользователь с таким именем уже существует!", thrown.getMessage());
    }
    /**
     * Тест регистрации с уже существующим адресом.
     */
    @Test
    void tryToRegisterTest_sameAddress() {
        Mockito.when(userRepo.findUserByFullAddress("city", "str", 11, 12))
                .thenReturn(new User("user", "user", Role.USER, "city", "str", 11, 12));
        RegistrationException thrown = Assertions.assertThrows(
                RegistrationException.class,
                () -> authService.tryToRegister("newUser", "user", Role.USER, "city", "str", 11, 12));
        Assertions.assertEquals("Пользователь с данным адресом уже существует!", thrown.getMessage());
    }
    /**
     * Тест успешной регистрации.
     */
    @Test
    void tryToRegisterTest_success() {
        Mockito.when(userRepo.findUserByLogin(Mockito.eq("newUser")))
                .thenReturn(null);
        Mockito.when(userRepo.findUserByFullAddress("city", "str", 11, 12))
                .thenReturn(null);
        try {
            authService.tryToRegister("newUser", "user", Role.USER, "city", "str", 11, 12);
        } catch (RegistrationException e) {
            Assertions.fail();
        }
    }
}
