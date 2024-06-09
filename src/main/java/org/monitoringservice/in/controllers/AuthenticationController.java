package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.dto.UserLoginDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Класс контроллера, который отвечает за авторизацию и регистрацию.
 * Обрабатывает запросы GET с адресом "api/logout", и запросы POST с адресами "/login", а также "/registration".
 */
@RestController
public class AuthenticationController {
    /**
     * Поле с сервисом аутентификации.
     */
    private final AuthenticationService authenticationService;

    /**
     * Конструктор для внедерния зависимости.
     */
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Метод, который обрабатывает запросы POST с адресом "/login".
     *
     * @param loginDTO DTO информации, нужной для авторизации
     * @param request  запрос
     * @return ResponseEntity&lt;Object&gt; - при успешной авторизации, возвращает статус 200(OK).
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     */
    @PostMapping(value = "/login", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) {
        try {
            String validation = DtoValidator.isValid(loginDTO);
            if (validation != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
            }

            HttpSession session = request.getSession();

            UserDTO user = authenticationService.login(loginDTO.getLogin(), loginDTO.getPassword());
            session.setAttribute("login", user.getLogin());
            session.setAttribute("role", user.getRole());
            session.setAttribute("id", user.getId());
        } catch (LoginException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешный логин");
    }

    /**
     * Метод, который обрабатывает запросы GET с адресом "api/logout".
     *
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - аннулирует сеанс и вывозвращает статус 200(OK).
     */
    @GetMapping(value = "api/logout", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Вы вышли из аккаунта!");
    }

    /**
     * Метод, который обрабатывает запросы POST с адресом "/registration".
     *
     * @param userDTO DTO информации, нужной для регистрации
     * @return ResponseEntity&lt;Object&gt; - при успешной регистрации, возвращает строку статус 200(OK).
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     */
    @PostMapping(value = "/registration", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> register(@RequestBody UserDTO userDTO) {
        String validation = DtoValidator.isValid(userDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        try {
            authenticationService.addNewUser(
                    userDTO.getLogin(),
                    userDTO.getPassword(),
                    Role.USER,
                    userDTO.getCity(),
                    userDTO.getStreet(),
                    userDTO.getHouseNumber(),
                    userDTO.getApartmentNumber()
            );
        } catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешно.");
    }
}
