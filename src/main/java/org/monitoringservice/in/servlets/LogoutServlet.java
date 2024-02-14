package org.monitoringservice.in.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Класс сервлета. Используется для выхода из аккаунта.
 */
@WebServlet("/api/logout")
public class LogoutServlet extends HttpServlet {
    /**
     * Метод, обрабатывабщий GET запрос.
     *
     * @param req  запрос к сервлету
     * @param resp ответ от сервлета
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().invalidate();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}