package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.NewReadoutDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.ReadoutException;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class NewReadoutController {
    private final MeterService meterService;

    @Autowired
    public NewReadoutController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping(value = "/api/readout", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> readout(@RequestBody NewReadoutDTO newReadoutDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("role") != Role.USER) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Только для Пользователей.");
        }

        int id;
        try {
            id = Integer.parseInt(session.getAttribute("id").toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка сервиса.");
        }

        String validation = DtoValidator.isValid(newReadoutDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        try {
            meterService.newReadout(id,newReadoutDTO.getType(), newReadoutDTO.getReadout());
        }catch (ReadoutException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешно.");
    }
}
