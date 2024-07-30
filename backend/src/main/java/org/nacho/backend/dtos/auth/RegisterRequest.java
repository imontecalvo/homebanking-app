package org.nacho.backend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.roles_authorities.RoleEnum;

import java.util.List;

@Builder
@Data
public class RegisterRequest {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @NotNull
    private String confirmPassword;

    @NotNull
    private Currency currency;

    @NotNull
    @Size.List({@Size(min = 1), @Size(max = 3)})
    private List<RoleEnum> roles;
}
