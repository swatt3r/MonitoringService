package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.MeterTypeDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.util.annotations.Loggable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

/**
 * Класс сервлета. Используется для обработки запросов на добавление нового типа счетчиков.
 */
@Loggable
@WebServlet("/api/newType")
public class NewTypeServlet extends HttpServlet {
    /**
     * Поле для хранения маппера.
     */
    private ObjectMapper objectMapper;
    /**
     * Поле для хранения сервиса счетчиков.
     */
    private MeterService meterService;

    /**
     * Метод инициализации сервлета.
     *
     * @param config - конфигурация
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
        meterService = (MeterService) config.getServletContext().getAttribute("meterService");
    }
    /**
     * Метод, обрабатывабщий POST запрос. Статус код возврата 200, если не было ошибок.
     *
     * @param req  запрос к сервлету
     * @param resp ответ от сервлета
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Role sessionRole = (Role) req.getSession().getAttribute("role");

        if (sessionRole != Role.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String json = req.getReader().lines().collect(joining());

        try {
            MeterTypeDTO meterTypeDTO = objectMapper.readValue(json, MeterTypeDTO.class);
            meterService.addNewType(meterTypeDTO.getType());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | MeterAddException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
