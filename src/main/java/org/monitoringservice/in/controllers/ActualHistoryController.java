package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.AdminSearchDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class ActualHistoryController {
    private final MeterService meterService;

    @Autowired
    public ActualHistoryController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping(value = "api/actual",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actual(HttpServletRequest request){
        HttpSession session = request.getSession();

        Role sessionRole = (Role) session.getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(session.getAttribute("id").toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка сервиса.");
        }

        return switch (sessionRole) {
            case USER -> ResponseEntity.ok(meterService.getUserActual(id));
            case ADMIN -> ResponseEntity.ok(meterService.getActualForAdmin(""));
        };
    }

    @PostMapping(value = "/api/actual", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actualForAdmin(@RequestBody AdminSearchDTO adminSearchDTO, HttpServletRequest request) {
        if (request.getSession().getAttribute("role") != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Только для Администратора.");
        }

        String validation = DtoValidator.isValid(adminSearchDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        return ResponseEntity.ok(meterService.getActualForAdmin(adminSearchDTO.getLogin()));
    }
}
