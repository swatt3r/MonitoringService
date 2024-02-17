package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class RegistrationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public RegistrationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/registration", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> register(@RequestBody UserDTO userDTO, HttpServletRequest request) {
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
        }catch (RegistrationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешно.");
    }
}
