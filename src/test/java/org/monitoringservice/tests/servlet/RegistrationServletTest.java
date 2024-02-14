package org.monitoringservice.tests.servlet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.in.servlets.RegistrationServlet;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServletTest extends ServletTest{
    @InjectMocks
    private RegistrationServlet registrationServlet;

    private static UserDTO user;

    @Test
    @DisplayName("Тест регистрации пользователя")
    public void registerTest() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("json"));
        when(req.getReader()).thenReturn(reader);

        when(objectMapper.readValue("json", UserDTO.class)).thenReturn(user);

        registrationServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @BeforeAll
    public static void init(){
        user = new UserDTO();
        user.setLogin("login");
        user.setPassword("password");
        user.setCity("city");
        user.setStreet("street");
        user.setHouseNumber(12);
        user.setApartmentNumber(21);
    }
}
