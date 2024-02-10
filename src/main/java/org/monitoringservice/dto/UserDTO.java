package org.monitoringservice.dto;

import lombok.Data;
import org.monitoringservice.entities.Role;

@Data
public class UserDTO {
    private String login;
    private String password;
    private Role role;
    private Integer id;
    private String city;
    private String street;
    private Integer houseNumber;
    private Integer apartmentNumber;
}
