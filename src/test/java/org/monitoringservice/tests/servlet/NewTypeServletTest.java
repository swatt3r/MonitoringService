package org.monitoringservice.tests.servlet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.dto.MeterTypeDTO;
import org.monitoringservice.entities.Role;
import org.monitoringservice.in.servlets.NewTypeServlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewTypeServletTest extends ServletTest{
    @InjectMocks
    private NewTypeServlet newTypeServlet;

    @Test
    @DisplayName("Тест регистрации нового типа счетчика")
    public void newTypeTest() throws Exception {
        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        when(session.getAttribute("role")).thenReturn(Role.ADMIN);

        BufferedReader reader = new BufferedReader(new StringReader("json"));
        when(req.getReader()).thenReturn(reader);

        MeterTypeDTO type = new MeterTypeDTO();
        type.setType("HeatT");
        when(objectMapper.readValue("json", MeterTypeDTO.class)).thenReturn(type);

        newTypeServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
