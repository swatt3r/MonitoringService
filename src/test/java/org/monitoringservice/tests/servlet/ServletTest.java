package org.monitoringservice.tests.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mock;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.MeterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletTest {
    @Mock
    protected MeterService meterService;

    @Mock
    protected AuthenticationService authService;

    @Mock
    protected HttpServletRequest req;

    @Mock
    protected HttpServletResponse resp;

    @Mock
    protected ObjectMapper objectMapper;
}
