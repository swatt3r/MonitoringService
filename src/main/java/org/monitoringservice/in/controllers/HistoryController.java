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

/**
 * Класс контроллера, который отвечает за историю показаний.
 * Обрабатывает запросы GET и POST, с адресом "api/history".
 */
@RestController
public class HistoryController {
    /**
     * Поле с сервисом для работы со счетчиками.
     */
    private final MeterService meterService;

    /**
     * Конструктор для внедерния зависимости.
     */
    @Autowired
    public HistoryController(MeterService meterService) {
        this.meterService = meterService;
    }

    /**
     * Метод, который обрабатывает запросы GET с адресом "api/history".
     *
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - ответ, который содержит строки с записями показаний счетчиков.
     * Или, если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).
     */
    @GetMapping(value = "api/history",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> history(HttpServletRequest request){
        HttpSession session = request.getSession();

        Role sessionRole = (Role) session.getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(session.getAttribute("id").toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка сервиса.");
        }

        return switch (sessionRole) {
            case USER -> ResponseEntity.ok(meterService.getUserHistory(id));
            case ADMIN -> ResponseEntity.ok(meterService.getHistoryForAdmin(""));
        };
    }

    /**
     * Метод, который обрабатывает запросы POST с адресом "api/history".
     *
     * @param adminSearchDTO DTO запроса администратора
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - ответ, который содержит строки с записями показаний счетчиков.
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     * <p>Если запрос посылает не администратор, то в ответе будет статус 401(UNAUTHORIZED).</p>
     */
    @PostMapping(value = "/api/history", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> historyForAdmin(@RequestBody AdminSearchDTO adminSearchDTO, HttpServletRequest request) {
        if (request.getSession().getAttribute("role") != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Только для Администратора.");
        }

        String validation = DtoValidator.isValid(adminSearchDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        return ResponseEntity.ok(meterService.getHistoryForAdmin(adminSearchDTO.getLogin()));
    }
}