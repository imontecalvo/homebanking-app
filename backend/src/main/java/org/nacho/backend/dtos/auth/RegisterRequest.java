package org.nacho.backend.dtos.auth;

import lombok.Data;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.roles_authorities.RoleEnum;

import java.util.List;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Currency currency;
    private List<RoleEnum> roles;
}
