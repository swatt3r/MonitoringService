package org.monitoringservice.tests.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.dto.UserLoginDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.in.servlets.LoginServlet;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.util.mapper.UserMapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServletTest extends ServletTest{
    @InjectMocks
    private LoginServlet loginServlet;

    private UserDTO user;

    private List<UserLoginDTO> userLoginList;

    @Test
    @DisplayName("Тест успешной авторизации")
    public void successLoginTest() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("json"));

        when(req.getReader()).thenReturn(reader);
        when(authService.login("user", "user")).thenReturn(user);
        when(objectMapper.readValue("json", UserLoginDTO.class)).thenReturn(userLoginList.get(0));

        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        loginServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест неудачной авторизации")
    public void failedLoginTest() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("json"));

        when(req.getReader()).thenReturn(reader);
        when(authService.login("user", "admin")).thenThrow(new LoginException("Неверный пароль"));
        when(objectMapper.readValue("json", UserLoginDTO.class)).thenReturn(userLoginList.get(1));

        loginServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_OK);
    }


    @BeforeEach
    public void createData(){
        userLoginList = new LinkedList<>();
        user = UserMapper.MAPPER.userToUserDTO(
                new User(-1,"user", "user", Role.USER, "city", "street", 12, 12)
        );

        UserLoginDTO first = new UserLoginDTO();
        first.setLogin("user");
        first.setPassword("user");
        userLoginList.add(first);

        UserLoginDTO second = new UserLoginDTO();
        second.setLogin("user");
        second.setPassword("admin");
        userLoginList.add(second);
    }
}