package org.monitoringservice.util;

import org.monitoringservice.dto.UserDTO;

/**
 * Класс валидотора. Используется для валидации UserDTO.
 */
public class DtoValidator {
    /**
     * Метод, обеспечивающий валидацию UserDTO.
     *
     * @param userDTO DTO пользователя
     * @return true - если DTO не содержит ошибок, иначе false
     */
    public static boolean isValid(UserDTO userDTO) {
        return userDTO != null &&
                userDTO.getLogin() != null && userDTO.getLogin().length() >= 4 &&
                userDTO.getPassword() != null && userDTO.getLogin().length() >= 5 &&
                userDTO.getCity() != null && userDTO.getCity().length() >= 2 &&
                userDTO.getStreet() != null && userDTO.getStreet().length() >= 2 &&
                userDTO.getHouseNumber() != null && userDTO.getHouseNumber() > 0 &&
                userDTO.getApartmentNumber() != null && userDTO.getApartmentNumber() > 0;
    }
}
