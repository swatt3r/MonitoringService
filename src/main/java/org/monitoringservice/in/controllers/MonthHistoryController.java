package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.MonthSearchDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class MonthHistoryController {
    private final MeterService meterService;

    @Autowired
    public MonthHistoryController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping(value = "/api/month", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> month(@RequestBody MonthSearchDTO monthSearchDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();

        Role sessionRole = (Role) session.getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(session.getAttribute("id").toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка сервиса.");
        }

        String validation = DtoValidator.isValid(monthSearchDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        return switch (sessionRole) {
            case USER -> ResponseEntity.ok(meterService.getUserMonthHistory(id, monthSearchDTO.getMonth()));
            case ADMIN -> ResponseEntity.ok(
                    meterService.getMonthHistoryForAdmin(monthSearchDTO.getLogin(), monthSearchDTO.getMonth()));
        };
    }
}
