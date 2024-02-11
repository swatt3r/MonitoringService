package org.monitoringservice.tests.servlet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monitoringservice.entities.Role;
import org.monitoringservice.in.servlets.ShowUserMetersServlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowUserMetersServletTest extends ServletTest{
    @InjectMocks
    private ShowUserMetersServlet showUserMetersServlet;

    @Test
    @DisplayName("Тест получения списка пользовательских счетчиков")
    public void successShowMetersTest() throws Exception {
        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        when(session.getAttribute("role")).thenReturn(Role.USER);
        when(session.getAttribute("id")).thenReturn(12);

        LinkedList<String> result = new LinkedList<>();
        result.add("res1");
        result.add("res2");

        when(meterService.getUserMeters(12)).thenReturn(result);

        ServletOutputStream outputStream = Mockito.mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(outputStream);

        showUserMetersServlet.doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(0)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
