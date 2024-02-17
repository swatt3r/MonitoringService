package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
public class LogoutController {
    @GetMapping(value = "api/logout",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(HttpServletRequest request){
        request.getSession().invalidate();
        return ResponseEntity.ok("Вы вышли из аккаунта!");
    }
}
