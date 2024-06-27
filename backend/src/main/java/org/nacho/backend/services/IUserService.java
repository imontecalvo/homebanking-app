package org.nacho.backend.services;

import org.nacho.backend.dtos.UserRegistrationDTO;
import org.nacho.backend.exceptions.InvalidInput;

public interface IUserService {

    public void newUser(UserRegistrationDTO userRegistrationDTO) throws InvalidInput;
}
