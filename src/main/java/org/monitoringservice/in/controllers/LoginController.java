package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.dto.UserLoginDTO;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class LoginController {
    private final AuthenticationService authenticationService;

    @Autowired
    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

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
}
