package org.monitoringservice.in;

import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.UserRepo;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * Класс контроллера, отвечающего за входящий поток данных.
 */
public class InputController {
    /** Поле с сервисом авторизации и регистрации. */
    private final AuthenticationService authService;
    /** Поле с сервисом для работы со счетчиками и показаниями. */
    private final MeterService meterService;
    /** Поле для хранения действий пользователей. */
    private final LinkedList<String> audit;
    /**
     * Создание контроллера.
     */
    public InputController() {
        UserRepo users = new UserRepo();
        this.authService = new AuthenticationService(users);
        this.meterService = new MeterService(users);
        audit = new LinkedList<>();
    }
    /**
     * Метод считывания команд пользователя в начальном меню.
     */
    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            menu();
            String userCommand = scanner.nextLine();
            switch (userCommand) {
                case "/login":
                    login(scanner);
                    break;
                case "/register":
                    register(scanner);
                    break;
                case "/exit":
                    return;
                default:
                    System.out.println("Такой операции не существует!");
            }
        }
    }
    /**
     * Метод вывода начального меню в консоль.
     */
    private void menu() {
        System.out.println(
                "Сервис для управления показаниями счетчиков. \n" +
                        "Введите одну из следующих команд:\n" +
                        "    - Войти в систему - /login\n" +
                        "    - Зарегистрироваться в системе - /register\n" +
                        "    - Закрыть сервис - /exit");
    }
    /**
     * Метод вывода пользовательского меню в консоль.
     */
    private void userServiceMenu(User user) {
        System.out.println(
                user.getLogin() + "\n" +
                        "Введите одну из следующих команд:\n" +
                        "    - Увидеть актуальные показания - /actual\n" +
                        "    - Добавить новый счетчик - /newMeter\n" +
                        "    - Показать имеющиеся счетчики - /showMeters\n" +
                        "    - Показать историю показаний - /history\n" +
                        "    - Показать историю показаний за месяц - /month\n" +
                        "    - Ввести новое показание - /input\n" +
                        "    - Выйти из аккаунта - /logout");
    }
    /**
     * Метод вывода меню администратора в консоль.
     */
    private void adminServiceMenu() {
        System.out.println(
                "Панель управления администратора." + "\n" +
                        "Введите одну из следующих команд:\n" +
                        "    - Увидеть актуальные показания - /actual\n" +
                        "    - Показать историю показаний - /history\n" +
                        "    - Показать историю показаний за месяц - /month\n" +
                        "    - Добавить новый тип счетчиков - /newType\n" +
                        "    - Выйти из аккаунта - /logout");
    }
    /**
     * Метод считывания команд пользователя в пользовательском меню.
     */
    private void userMenu(User user, Scanner scanner) {
        while (true) {
            userServiceMenu(user);
            String userCommand = scanner.nextLine();
            switch (userCommand) {
                case "/actual":
                    userActual(user);
                    break;
                case "/newMeter":
                    userAddMeter(user, scanner);
                    break;
                case "/showMeters":
                    showMeter(user);
                    break;
                case "/input":
                    newReadout(user, scanner);
                    break;
                case "/history":
                    userHistory(user);
                    break;
                case "/month":
                    userMonth(user, scanner);
                    break;
                case "/logout":
                    audit.add("Пользователь " + user.getLogin() + " вышел из аккаунта");
                    return;
                default:
                    System.out.print("Такой операции не существует!");
            }
        }
    }
    /**
     * Метод считывания команд администратора в меню администротора.
     */
    private void adminMenu(Scanner scanner) {
        while (true) {
            adminServiceMenu();
            String adminCommand = scanner.nextLine();
            switch (adminCommand) {
                case "/actual":
                    adminActual(scanner);
                    break;
                case "/history":
                    adminHistory(scanner);
                    break;
                case "/month":
                    adminMonth(scanner);
                    break;
                case "/newType":
                    newType(scanner);
                    break;
                case "/logout":
                    audit.add("Администратор вышел из аккаунта");
                    return;
                default:
                    System.out.print("Такой операции не существует!");
            }
        }
    }
    /**
     * Метод, обеспечивающий функцию добавления нового типа счетчика. Используется администратором
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void newType(Scanner scanner) {
        System.out.print("Введите название нового типа счетчика: ");
        String type = scanner.nextLine();
        if (type.isEmpty()) {
            System.out.println("Отсутствует название счетчика!");
            return;
        }
        meterService.addNewType(type);
        audit.add("Администротор добавил новый тип счетчиков");
    }
    /**
     * Метод, обеспечивающий функцию поиска и вывода актуальных показаний счетчиков пользователя.
     * @param user - пользователь
     */
    private void userActual(User user) {
        audit.add("Пользователь " + user.getLogin() + " получил актуальные показания");
        LinkedList<String> result = meterService.getUserActual(user);
        if (!result.isEmpty()) {
            for (String line : result) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствуют актуальные показания");
        }
    }
    /**
     * Метод, обеспечивающий функцию поиска и вывода актуальных показаний счетчиков пользователей. Используется администратором
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminActual(Scanner scanner) {
        audit.add("Администратор получил актуальные показания");
        System.out.print("Введите логин пользователя, или оставте пустым, для вывода акутуальных значений всех пользователей: ");
        String login = scanner.nextLine();
        LinkedList<String> actual = meterService.getActualForAdmin(login);
        if (!actual.isEmpty()) {
            for (String line : actual) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствуют показания или неверно указан логин.");
        }
    }
    /**
     * Метод, обеспечивающий функцию добавления нового счетчика пользователю.
     * @param user - пользователь
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void userAddMeter(User user, Scanner scanner) {
        System.out.println("Введите один из типов счетчика:");
        for (String type : meterService.getMeterTypes()) {
            System.out.print(" - " + type + "\n");
        }
        try {
            meterService.addNewMeterToUser(user, scanner.nextLine());
            audit.add("Пользователь " + user.getLogin() + " добавил новый счетчик");
        } catch (MeterAddException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Метод, обеспечивающий функцию вывода списка счетчиков пользователя.
     * @param user - пользователь
     */
    private void showMeter(User user) {
        audit.add("Пользователь " + user.getLogin() + " посмотрел свой список счетчиков");
        LinkedList<String> meters = meterService.getUserMeters(user);
        if (!meters.isEmpty()) {
            System.out.println("На вас зарегистрированы следующие счетчики:");
            for (String type : meters) {
                System.out.print(" - " + type + "\n");
            }
        } else {
            System.out.println("У вас нет счетчиков.");
        }
    }
    /**
     * Метод, обеспечивающий функцию добавления нового показания пользователя.
     * @param user - пользователь
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void newReadout(User user, Scanner scanner) {
        System.out.println("Введите один из типов счетчика:");
        LinkedList<String> meters = meterService.getUserMeters(user);
        if (meters.isEmpty()) {
            System.out.println("Отсутствуют счетчики!");
        }
        for (String type : meters) {
            System.out.print(" - " + type + "\n");
        }
        String type = scanner.nextLine();
        System.out.print("Введите показание счетчика:");
        try {
            meterService.newReadout(user, type, Integer.parseInt(scanner.nextLine()));
            audit.add("Пользователь " + user.getLogin() + " внес новое показание");
        } catch (ReadoutException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Метод, обеспечивающий функцию вывода истории показаний пользователя.
     * @param user - пользователь
     */
    private void userHistory(User user) {
        audit.add("Пользователь " + user.getLogin() + " получил историю показаний");
        LinkedList<String> history = meterService.getUserHistory(user);
        if (!history.isEmpty()) {
            for (String line : history) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствует история показаний!");
        }
    }
    /**
     * Метод, обеспечивающий функцию вывода истории показаний пользователей. Используется администратором
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminHistory(Scanner scanner) {
        audit.add("Администратор получил историю показаний");
        System.out.print("Введите логин пользователя, или оставте пустым, для вывода истории всех пользователей: ");
        String login = scanner.nextLine();
        LinkedList<String> history = meterService.getHistoryForAdmin(login);
        if (!history.isEmpty()) {
            for (String line : history) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствуют показания или неверно указан логин.");
        }
    }
    /**
     * Метод, обеспечивающий функцию вывода истории показаний пользователя за месяц.
     * @param user - пользователь
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void userMonth(User user, Scanner scanner) {
        System.out.println("Введите номер месяца:");
        int month;
        try {
            month = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ввод не является целым числом!");
            return;
        }
        if (month > 0 && month < 13) {
            audit.add("Пользователь " + user.getLogin() + " получил историю показаний за месяц");
            LinkedList<String> monthHistory = meterService.getUserMonthHistory(user, month);
            if (!monthHistory.isEmpty()) {
                for (String line : monthHistory) {
                    System.out.println(line);
                }
            } else {
                System.out.println("Отсутствует история показаний c указаннымим параметрами!");
            }
        } else {
            System.out.println("Месяц указан неверно!");
        }
    }
    /**
     * Метод, обеспечивающий функцию вывода истории показаний пользователей за месяц. Используется администратором
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminMonth(Scanner scanner) {
        audit.add("Администратор получил историю показаний за месяц");
        System.out.print("Введите логин пользователя, или оставте пустым, для вывода месячной истории всех пользователей: ");
        String login = scanner.nextLine();
        System.out.print("Введите номер месяца: ");
        int month;
        try {
            month = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ввод не является целым числом!");
            return;
        }
        LinkedList<String> history = meterService.getMonthHistoryForAdmin(login, month);
        if (!history.isEmpty()) {
            for (String line : history) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствуют показания или неверно указан логин/месяц.");
        }
    }
    /**
     * Метод, обеспечивающий функцию авторизации пользователя.
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void login(Scanner scanner) {
        while (true) {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();
            User user;
            try {
                user = authService.tryToLogin(login, password);
            } catch (LoginException e) {
                System.out.print(e.getMessage() + "\n");
                continue;
            }
            System.out.println("Успешная авторизация.");
            audit.add("Пользователь " + login + " авторизовался");
            if (user.getRole() == Role.USER) {
                userMenu(user, scanner);
            } else {
                adminMenu(scanner);
            }
            break;
        }
    }
    /**
     * Метод, обеспечивающий функцию регистрации пользователя.
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void register(Scanner scanner) {
        while (true) {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine();
            if (login.length() < 3) {
                System.out.println("Логин не может быть короче 3 символов");
                continue;
            }

            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();
            if (password.length() < 5) {
                System.out.println("Пароль не может быть короче 5 символов");
                continue;
            }

            System.out.print("Введите город: ");
            String city = scanner.nextLine();
            if (city.length() < 2) {
                System.out.println("Название города не может быть короче 2 символов");
                continue;
            }

            System.out.print("Введите улицу: ");
            String street = scanner.nextLine();
            if (street.length() < 2) {
                System.out.println("Название улицы не может быть короче 2 символов");
                continue;
            }

            System.out.print("Введите номер дома: ");
            int houseNumber = Integer.parseInt(scanner.nextLine());
            if (houseNumber < 1) {
                System.out.println("Номер дома не может быть меньше 1");
                continue;
            }

            System.out.print("Введите номер квартиры: ");
            int apartmentNumber = Integer.parseInt(scanner.nextLine());
            if (apartmentNumber < 1) {
                System.out.println("Номер квартиры не может быть меньше 1");
                continue;
            }

            try {
                authService.tryToRegister(login, password, Role.USER, city, street, houseNumber, apartmentNumber);
            } catch (RegistrationException | NumberFormatException e) {
                System.out.print(e.getMessage() + "\n");
                continue;
            }

            System.out.println("Успешная Регистрация.");
            audit.add("Пользователь " + login + " зарегистрировался");
            break;
        }
    }

}