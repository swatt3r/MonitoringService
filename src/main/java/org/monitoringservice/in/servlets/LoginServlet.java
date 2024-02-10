package org.monitoringservice.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monitoringservice.dto.UserLoginDTO;
import org.monitoringservice.entities.User;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.authexceptions.LoginException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.stream.Collectors.joining;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private AuthenticationService authenticationService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
        authenticationService = (AuthenticationService) config.getServletContext().getAttribute("authService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String json = req.getReader().lines().collect(joining());

        try {
            UserLoginDTO userLoginDTO = objectMapper.readValue(json, UserLoginDTO.class);

            //Использовать MAPSTRUCT
            User user = authenticationService.login(userLoginDTO.getLogin(), userLoginDTO.getPassword());
            req.getSession().setAttribute("login", user.getLogin());
            req.getSession().setAttribute("role", user.getRole());
            req.getSession().setAttribute("id", user.getId());
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (LoginException | IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
