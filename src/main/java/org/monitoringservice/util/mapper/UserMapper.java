package org.monitoringservice.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.monitoringservice.dto.UserDTO;
import org.monitoringservice.entities.User;

/**
 * Интерфейс маппера. Используется для преобразования User в UserDTO.
 */
@Mapper
public interface UserMapper {
    /**
     * Поле для хранения маппера. Создано, чтобы не писать реализацию интерфейса.
     */
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
    /**
     * Метод, обеспечивающий функцию преобразования User в UserDTO.
     *
     * @param user пользователь
     * @return UserDTO полученный из user
     */
    @Mapping(target = "login", source = "login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "houseNumber", source = "houseNumber")
    @Mapping(target = "apartmentNumber", source = "apartmentNumber")
    UserDTO userToUserDTO(User user);
}
