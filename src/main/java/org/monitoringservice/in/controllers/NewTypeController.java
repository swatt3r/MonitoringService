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
 * Класс контроллера, который отвечает за регистрацию новых типов счетчиков.
 * Обрабатывает запросы POST, с адресом "api/newType".
 */
@RestController
public class NewTypeController {
    /**
     * Поле с сервисом для работы со счетчиками.
     */
    private final MeterService meterService;
    /**
     * Конструктор для внедерния зависимости.
     */
    @Autowired
    public NewTypeController(MeterService meterService) {
        this.meterService = meterService;
    }
    /**
     * Метод, который обрабатывает запросы POST с адресом "api/newType". Совершает регистрацию новых типов счетчиков.
     *
     * @param meterTypeDTO DTO для типа счетчика
     * @param request запрос
     * @return ResponseEntity&lt;Object&gt; - при успешной регистрации нового типа счетчика возвращает статус 200(OK).
     * <p>Если запрос посылает не администратор, то в ответе будет статус 401(UNAUTHORIZED).</p>
     * <p>Если совершена ошибка в запросе, возвращает статус 400(BAD_REQUEST).</p>
     */
    @PostMapping(value = "/api/newType", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newType(@RequestBody MeterTypeDTO meterTypeDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("role") != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Только для Администратора.");
        }

        String validation = DtoValidator.isValid(meterTypeDTO);
        if (validation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validation);
        }

        try {
            meterService.addNewType(meterTypeDTO.getType());
        }catch (MeterAddException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("Успешно.");
    }
}
