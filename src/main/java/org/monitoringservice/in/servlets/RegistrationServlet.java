package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.util.DtoValidator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private ObjectMapper objectMapper;
    private AuthenticationService authenticationService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
        authenticationService = (AuthenticationService) config.getServletContext().getAttribute("authService");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String json = req.getReader().lines().collect(joining());

        try {
            UserDTO userDTO = objectMapper.readValue(json, UserDTO.class);
            if(!DtoValidator.isValid(userDTO)){
                throw new IOException();
            }
            authenticationService.register(
                    userDTO.getLogin(),
                    userDTO.getPassword(),
                    Role.USER,
                    userDTO.getCity(),
                    userDTO.getStreet(),
                    userDTO.getHouseNumber(),
                    userDTO.getApartmentNumber()
            );
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (RegistrationException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}