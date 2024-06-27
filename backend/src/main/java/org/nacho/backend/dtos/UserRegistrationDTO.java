package org.nacho.backend.dtos;

import lombok.Data;
import org.nacho.backend.models.Currency;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String email;
    private Currency currency;
}
