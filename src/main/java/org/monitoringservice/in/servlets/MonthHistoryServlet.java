package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.MonthSearchDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

@WebServlet("/api/month")
public class MonthHistoryServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private MeterService meterService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
        meterService = (MeterService) config.getServletContext().getAttribute("meterService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Role sessionRole = (Role) req.getSession().getAttribute("role");
        int id = (int) req.getSession().getAttribute("id");

        String json = req.getReader().lines().collect(joining());

        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            MonthSearchDTO monthSearchDTO = objectMapper.readValue(json, MonthSearchDTO.class);
            switch (sessionRole) {

                case USER:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(
                                    meterService.getUserMonthHistory(id, monthSearchDTO.getMonth()))
                            );
                    break;

                case ADMIN:
                    if(monthSearchDTO.getLogin() == null){
                        throw new IOException();
                    }
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(
                                    meterService.getMonthHistoryForAdmin(monthSearchDTO.getLogin(), monthSearchDTO.getMonth()))
                            );
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
