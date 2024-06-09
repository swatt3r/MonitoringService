package org.monitoringservice.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AOPLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(public * org.monitoringservice.in.controllers.*.*(..))")
    public Object doLog(ProceedingJoinPoint join) throws Throwable {
        HttpServletRequest request = null;
        Object reqBody = null;
        for (Object arg : join.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            } else {
                reqBody = arg;
            }
        }
        if (request == null) {
            return null;
        }

        HttpSession session = request.getSession();
        StringBuilder logInfo = new StringBuilder();

        logInfo.append("REQ-").append(request.getMethod()).append(": ")
                .append("\"").append(request.getRequestURI()).append("\", ")
                .append("ID: ").append(session.getAttribute("id")).append(", ")
                .append("LOGIN: ").append(session.getAttribute("login")).append(", ")
                .append("ROLE: ").append(session.getAttribute("role")).append(", ")
                .append("BODY: ").append(reqBody).append(".")
                .append("\n");

        Object returningValue = join.proceed();

        ResponseEntity<Object> response = (ResponseEntity<Object>) returningValue;

        logInfo.append("RES: ")
                .append("\"").append(request.getRequestURI()).append("\", ")
                .append("ID: ").append(session.getAttribute("id")).append(", ")
                .append("LOGIN: ").append(session.getAttribute("login")).append(", ")
                .append("ROLE: ").append(session.getAttribute("role")).append(", ")
                .append("STATUS: ").append(response.getStatusCode()).append(", ")
                .append("BODY: ").append(response.getBody()).append(".");

        logger.info(logInfo.toString());
        return returningValue;
    }
}