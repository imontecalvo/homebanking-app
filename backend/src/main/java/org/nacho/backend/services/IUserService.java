package org.nacho.backend.services;

import org.nacho.backend.dtos.UserRegistrationDTO;
import org.nacho.backend.exceptions.UnavailableField;

public interface IUserService {

    public void newUser(UserRegistrationDTO userRegistrationDTO) throws UnavailableField;
}
