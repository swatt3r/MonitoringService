package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.MonthSearchDTO;
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
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Класс сервлета. Используется для обработки запросов на историю показаний за месяц.
 */
@WebServlet("/api/month")
public class MonthHistoryServlet extends HttpServlet {
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
     * Метод, обрабатывабщий POST запрос. В ответ посылает историю показаний за месяц, если не было ошибок.
     *
     * @param req  запрос к сервлету
     * @param resp ответ от сервлета
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Role sessionRole = (Role) req.getSession().getAttribute("role");
        int id;
        try {
            id = Integer.parseInt(req.getSession().getAttribute("id").toString());
        }catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String json = req.getReader().lines().collect(joining());

        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            MonthSearchDTO monthSearchDTO = objectMapper.readValue(json, MonthSearchDTO.class);

            String validation = DtoValidator.isValid(monthSearchDTO);
            if(validation != null){
                resp.getWriter().println(validation);
                throw  new IOException();
            }

            switch (sessionRole) {
                case USER:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(
                                    meterService.getUserMonthHistory(id, monthSearchDTO.getMonth()))
                            );
                    break;
                case ADMIN:
                    if (monthSearchDTO.getLogin() == null) {
                        throw new IOException();
                    }
                    List<String> history = meterService.getMonthHistoryForAdmin(
                            monthSearchDTO.getLogin(),
                            monthSearchDTO.getMonth()
                    );
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(history));
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    break;
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}