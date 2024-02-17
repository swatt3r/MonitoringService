package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class ShowMetersController {
    private final MeterService meterService;

    @Autowired
    public ShowMetersController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping(value = "api/showMeters",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> showMeters(HttpServletRequest request){
        HttpSession session = request.getSession();

        Role sessionRole = (Role) session.getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(session.getAttribute("id").toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка сервиса.");
        }

        return switch (sessionRole) {
            case USER -> ResponseEntity.ok(meterService.getUserMeters(id));
            case ADMIN -> ResponseEntity.ok(meterService.getMeterTypes());
        };
    }
}
