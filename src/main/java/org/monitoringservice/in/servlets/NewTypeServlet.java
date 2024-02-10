package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.MeterTypeDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.meterexecptions.MeterAddException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

@WebServlet("/api/newType")
public class NewTypeServlet extends HttpServlet {
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

        if (sessionRole != Role.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String json = req.getReader().lines().collect(joining());

        try {
            MeterTypeDTO meterTypeDTO = objectMapper.readValue(json, MeterTypeDTO.class);
            resp.setStatus(HttpServletResponse.SC_OK);
            meterService.addNewType(meterTypeDTO.getType());
        } catch (IOException | MeterAddException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
