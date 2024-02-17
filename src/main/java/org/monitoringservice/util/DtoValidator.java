package org.monitoringservice.util;

import org.monitoringservice.dto.*;

/**
 * Класс валидотора. Используется для валидации UserDTO.
 */
public class DtoValidator {
    /**
     * Метод, обеспечивающий валидацию UserDTO.
     *
     * @param userDTO DTO пользователя
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(UserDTO userDTO) {
        try {
            if(userDTO.getLogin().length() < 4){
                return "Логин слишком короткий!";
            }
            if (userDTO.getPassword().length() < 4){
                return "Пароль слишком короткий!";
            }
            if (userDTO.getCity().length() < 2){
                return "Название города слишком короткое!";
            }
            if (userDTO.getStreet().length() < 2){
                return "Название улицы слишком коротое!";
            }
            if (userDTO.getHouseNumber() < 1){
                return "Номер дома не может быть меньше 1!";
            }
            if (userDTO.getApartmentNumber() < 1){
                return "Номер квартиры не может быть меньше 1!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }

    /**
     * Метод, обеспечивающий валидацию AdminSearchDTO.
     *
     * @param adminSearchDTO DTO поиска администратора
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(AdminSearchDTO adminSearchDTO) {
        try {
            if(adminSearchDTO.getLogin().length() < 4){
                return "Логин слишком короткий!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }
    /**
     * Метод, обеспечивающий валидацию MeterTypeDTO.
     *
     * @param meterTypeDTO DTO типа счетчика
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(MeterTypeDTO meterTypeDTO) {
        try {
            if(meterTypeDTO.getType().isEmpty()){
                return "Название типа отсутствует!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }
    /**
     * Метод, обеспечивающий валидацию MonthSearchDTO.
     *
     * @param monthSearchDTO DTO поиска показаний за месяц
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(MonthSearchDTO monthSearchDTO) {
        try {
            if(monthSearchDTO.getMonth() < 0 || monthSearchDTO.getMonth() > 12){
                return "Такого месяца не существует!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }

    /**
     * Метод, обеспечивающий валидацию NewReadoutDTO.
     *
     * @param newReadoutDTO DTO нового показания
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(NewReadoutDTO newReadoutDTO) {
        try {
            if(newReadoutDTO.getType().isEmpty()){
                return "Название типа отсутствует!";
            }
            if(newReadoutDTO.getReadout() < 0){
                return "Показание меньше нуля!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }

    /**
     * Метод, обеспечивающий валидацию UserLoginDTO.
     *
     * @param userLoginDTO DTO авторизованного пользователя
     * @return null - если DTO не содержит ошибок, иначе строка с сообщением об ошибке.
     */
    public static String isValid(UserLoginDTO userLoginDTO) {
        try {
            if(userLoginDTO.getLogin().length() < 4){
                return "Логин слишком короткий!";
            }
            if (userLoginDTO.getPassword().length() < 4){
                return "Пароль слишком короткий!";
            }
        }catch (NullPointerException e){
            return "Один из параметров отсутствует!";
        }
        return null;
    }
}
