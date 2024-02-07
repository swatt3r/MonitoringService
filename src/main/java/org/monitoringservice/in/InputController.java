package org.monitoringservice.in;

import org.monitoringservice.entities.Role;
import org.monitoringservice.entities.TypeOfAction;
import org.monitoringservice.entities.User;
import org.monitoringservice.repositories.MeterRepository;
import org.monitoringservice.repositories.UserRepository;
import org.monitoringservice.services.AuditService;
import org.monitoringservice.services.AuthenticationService;
import org.monitoringservice.services.MeterService;
import org.monitoringservice.services.authexceptions.LoginException;
import org.monitoringservice.services.authexceptions.RegistrationException;
import org.monitoringservice.services.meterexecptions.MeterAddException;
import org.monitoringservice.services.meterexecptions.ReadoutException;
import org.monitoringservice.util.MigrationUtil;
import org.monitoringservice.util.PropertiesUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Класс контроллера, отвечающего за входящий поток данных.
 */
public class InputController {
    /**
     * Поле с сервисом авторизации и регистрации.
     */
    private final AuthenticationService authService;
    /**
     * Поле с сервисом для работы со счетчиками и показаниями.
     */
    private final MeterService meterService;

    private final AuditService audit;

    /**
     * Создание контроллера.
     */
    public InputController() {
        UserRepository users = new UserRepository();
        MeterRepository history = new MeterRepository();

        authService = new AuthenticationService(users);
        meterService = new MeterService(users, history);

        audit = new AuditService();

        Properties properties = PropertiesUtil.getApplicationProperties();
        MigrationUtil.migrateDB(properties);
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
                    audit.saveAction(user.getLogin(), TypeOfAction.LogOut);
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
                    audit.saveAction("admin", TypeOfAction.LogOut);
                    return;
                default:
                    System.out.print("Такой операции не существует!");
            }
        }
    }

    /**
     * Метод, обеспечивающий функцию добавления нового типа счетчика. Используется администратором
     *
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
        audit.saveAction("admin", TypeOfAction.NewMeter);
    }

    /**
     * Метод, обеспечивающий функцию поиска и вывода актуальных показаний счетчиков пользователя.
     *
     * @param user - пользователь
     */
    private void userActual(User user) {
        audit.saveAction(user.getLogin(), TypeOfAction.Actual);
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
     *
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminActual(Scanner scanner) {
        audit.saveAction("admin", TypeOfAction.Actual);
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
     *
     * @param user    - пользователь
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void userAddMeter(User user, Scanner scanner) {
        System.out.println("Введите один из типов счетчика:");
        for (String type : meterService.getMeterTypes()) {
            System.out.print(" - " + type + "\n");
        }
        try {
            meterService.addNewMeterToUser(user, scanner.nextLine());
            System.out.println("Успешное добавление нового типа счетчика!");
            audit.saveAction(user.getLogin(), TypeOfAction.AddMeter);
        } catch (MeterAddException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод, обеспечивающий функцию вывода списка счетчиков пользователя.
     *
     * @param user - пользователь
     */
    private void showMeter(User user) {
        audit.saveAction(user.getLogin(), TypeOfAction.ShowUserMeter);
        List<String> meters = meterService.getUserMeters(user);
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
     *
     * @param user    - пользователь
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void newReadout(User user, Scanner scanner) {

        System.out.println("Введите один из типов счетчика:");
        List<String> meters = meterService.getUserMeters(user);
        if (meters.isEmpty()) {
            System.out.println("Отсутствуют счетчики!");
            return;
        }
        for (String type : meters) {
            System.out.print(" - " + type + "\n");
        }
        String type = scanner.nextLine();
        System.out.print("Введите показание счетчика:");
        try {
            meterService.newReadout(user, type, Integer.parseInt(scanner.nextLine()));
            System.out.println("Показание успешно внесено!");
            audit.saveAction(user.getLogin(), TypeOfAction.Readout);
        } catch (ReadoutException e) {
            System.out.println(e.getMessage());
        }catch (NumberFormatException e){
            System.out.println("Неправильный ввод показания!");
        }
    }

    /**
     * Метод, обеспечивающий функцию вывода истории показаний пользователя.
     *
     * @param user - пользователь
     */
    private void userHistory(User user) {
        audit.saveAction(user.getLogin(), TypeOfAction.History);
        List<String> history = meterService.getUserHistory(user);
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
     *
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminHistory(Scanner scanner) {
        audit.saveAction("admin", TypeOfAction.Actual);
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
     *
     * @param user    - пользователь
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
            audit.saveAction(user.getLogin(), TypeOfAction.MonthHistory);
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
     *
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void adminMonth(Scanner scanner) {
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
            audit.saveAction("admin", TypeOfAction.MonthHistory);
            for (String line : history) {
                System.out.println(line);
            }
        } else {
            System.out.println("Отсутствуют показания или неверно указан логин/месяц.");
        }
    }

    /**
     * Метод, обеспечивающий функцию авторизации пользователя.
     *
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
                user = authService.login(login, password);
            } catch (LoginException e) {
                System.out.print(e.getMessage() + "\n");
                System.out.println("Если желаете выйти из авторизации напишите - /quit.");
                System.out.println("Если желаете продолжить любой другой ввод.");
                String command = scanner.nextLine();
                if (command.equals("/quit")) {
                    System.out.println("Выход из меню авторизации.");
                    break;
                }
                continue;
            }
            System.out.println("Успешная авторизация.");
            audit.saveAction(user.getLogin(), TypeOfAction.Login);
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
     *
     * @param scanner - сканер, используется для считывания ввода с консоли
     */
    private void register(Scanner scanner) {
        while (true) {
            String login = "";
            while (login.length() < 3) {
                System.out.print("Введите логин: ");
                login = scanner.nextLine();
                if (login.length() < 3) {
                    System.out.println("Логин не может быть короче 3 символов");
                }
            }

            String password = "";
            while (password.length() < 5) {
                System.out.print("Введите пароль: ");
                password = scanner.nextLine();
                if (password.length() < 5) {
                    System.out.println("Пароль не может быть короче 5 символов");
                }
            }

            String city = "";
            while (city.length() < 2) {
                System.out.print("Введите название города: ");
                city = scanner.nextLine();
                if (city.length() < 2) {
                    System.out.println("Название города не может быть короче 2 символов");
                }
            }

            String street = "";
            while (street.length() < 2) {
                System.out.print("Введите название улицы: ");
                street = scanner.nextLine();
                if (street.length() < 2) {
                    System.out.println("Название улицы не может быть короче 2 символов");
                }
            }

            int houseNumber = 0;
            while (houseNumber < 1) {
                System.out.print("Введите номер дома: ");
                try {
                    houseNumber = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    System.out.println("Введено не целое число!");
                    continue;
                }
                if (houseNumber < 1) {
                    System.out.println("Номер дома не может быть меньше 1");
                }
            }

            int apartmentNumber = 0;
            while (apartmentNumber < 1) {
                System.out.print("Введите номер квартиры: ");
                try {
                    apartmentNumber = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    System.out.println("Введено не целое число!");
                    continue;
                }
                if (apartmentNumber < 1) {
                    System.out.println("Номер квартиры не может быть меньше 1");
                }
            }

            try {
                authService.register(login, password, Role.USER, city, street, houseNumber, apartmentNumber);
            } catch (RegistrationException | NumberFormatException e) {
                System.out.print(e.getMessage() + "\n");
                continue;
            }

            System.out.println("Успешная Регистрация.");

            audit.saveAction(login, TypeOfAction.Register);
            break;
        }
    }

}