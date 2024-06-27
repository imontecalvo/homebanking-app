package org.nacho.backend.services;

import org.nacho.backend.dtos.UserRegistrationDTO;
import org.nacho.backend.exceptions.UnavailableField;
import org.nacho.backend.models.User;

public interface IUserService {

    public void createUser(UserRegistrationDTO userRegistrationDTO) throws UnavailableField;
}
