package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/showMeters")
public class ShowUserMetersServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private MeterService meterService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
        meterService = (MeterService) config.getServletContext().getAttribute("meterService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        Role sessionRole = (Role) req.getSession().getAttribute("role");
        int id = (int) req.getSession().getAttribute("id");

        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            switch (sessionRole) {
                case USER:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(meterService.getUserMeters(id)));
                    break;
                case ADMIN:
                    resp.getOutputStream()
                            .write(objectMapper.writeValueAsBytes(meterService.getMeterTypes()));
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
