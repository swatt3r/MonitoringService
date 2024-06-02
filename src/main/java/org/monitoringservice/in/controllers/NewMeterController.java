package org.monitoringservice.in.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.monitoringservice.dto.MeterTypeDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.util.DtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Класс контроллера, который отвечает за регистрацию новых счетчиков на пользователей.
 * Обрабатывает запросы POST, с адресом "api/newMeter".
 */
@RestController
public class NewMeterController {
    /**
     * Поле с сервисом для работы со счетчиками.
     */
    private final MeterService meterService;
    /**
     * Конструктор для внедерния зависимости.
     */
    @Autowired
    public NewMeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    /**
     * Метод, который обрабатывает запросы POST с адресом "api/newMeter".
     *
     * @param meterTypeDTO DTO для типа счетчика
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - при успешной регистрации счетчика возвращает статус 200(OK).
     * <p>Если запрос посылает не пользователь, то в ответе будет статус 401(UNAUTHORIZED).</p>
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     */
    @PostMapping(value = "/api/newMeter", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newMeter(@RequestBody MeterTypeDTO meterTypeDTO, HttpServletRequest request) {
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

        String validation = DtoValidator.isValid(meterTypeDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        try {
            meterService.addNewMeterToUser(id, meterTypeDTO.getType());
        }catch (MeterAddException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешно.");
    }
}
