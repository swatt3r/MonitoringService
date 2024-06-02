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

/**
 * Класс контроллера, который отвечает за список всех счетчиков.
 * Обрабатывает запросы GET, с адресом "api/showMeters".
 */
@RestController
public class ShowMetersController {
    /**
     * Поле с сервисом для работы со счетчиками.
     */
    private final MeterService meterService;

    /**
     * Конструктор для внедерния зависимости.
     */
    @Autowired
    public ShowMetersController(MeterService meterService) {
        this.meterService = meterService;
    }

    /**
     * Метод, который обрабатывает запросы GET с адресом "api/showMeters".
     *
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - возвращает статус 200(OK). Для пользователя - все его счетчики.
     * Для администратора - все типы счетчиков.
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     */
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
