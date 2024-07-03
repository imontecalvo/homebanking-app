package org.nacho.backend.services;

import org.nacho.backend.dtos.auth.AuthResponse;
import org.nacho.backend.dtos.auth.LoginRequest;
import org.nacho.backend.dtos.auth.RegisterRequest;
import org.nacho.backend.exceptions.InvalidInput;

public interface IUserService {

    public AuthResponse newUser(RegisterRequest userRegistrationDTO) throws InvalidInput;

    public AuthResponse login(LoginRequest loginRequest);
}
