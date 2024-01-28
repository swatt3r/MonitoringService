package org.monitoringservice;

import org.monitoringservice.in.InputController;

/**
 * Класс приложения.
 */
public class App {
    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) {
        InputController input = new InputController();
        input.mainMenu();
    }
}
