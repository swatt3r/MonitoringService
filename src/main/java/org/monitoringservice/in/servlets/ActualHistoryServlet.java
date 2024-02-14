package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.AdminSearchDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.util.DtoValidator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

/**
 * Класс сервлета. Используется для обработки запросов на актуальную историю показаний.
 */
@WebServlet("/api/actual")
public class ActualHistoryServlet extends HttpServlet {
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
     * Метод, обрабатывабщий GET запрос. В ответ посылает актуальную историю показаний, если не было ошибок.
     *
     * @param req   запрос к сервлету
     * @param resp  ответ от сервлета
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        Role sessionRole = (Role) req.getSession().getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(req.getSession().getAttribute("id").toString());
        }catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            switch (sessionRole) {
                case USER:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(meterService.getUserActual(id)));
                    break;
                case ADMIN:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(meterService.getActualForAdmin("")));
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    break;
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Метод, обрабатывабщий POST запрос. В ответ посылает актуальную историю показаний, если не было ошибок.
     *
     * @param req   запрос к сервлету
     * @param resp  ответ от сервлета
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        if (req.getSession().getAttribute("role") != Role.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String json = req.getReader().lines().collect(joining());

        try {
            AdminSearchDTO adminSearchDTO = objectMapper.readValue(json, AdminSearchDTO.class);
            String validation = DtoValidator.isValid(adminSearchDTO);
            if(validation != null){
                resp.getWriter().println(validation);
                throw  new IOException();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getOutputStream()
                    .write(objectMapper.writeValueAsBytes(meterService.getActualForAdmin(adminSearchDTO.getLogin())));
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}