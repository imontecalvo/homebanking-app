package org.nacho.backend.services.impl;

import org.nacho.backend.dtos.UserRegistrationDTO;
import org.nacho.backend.exceptions.UnavailableField;
import org.nacho.backend.models.Balance;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.User;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.nacho.backend.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IBalanceRepository balanceRepository;

    private final BigDecimal INIT_BALANCE = BigDecimal.valueOf(2000);

    @Override
    public void newUser(UserRegistrationDTO userRegistrationDTO) throws UnavailableField {
        if (userRepository.existsByUsername(userRegistrationDTO.getUsername())) {
            throw new UnavailableField("Username already exists");
        } else if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new UnavailableField("Email already exists");
        }

        User user = User.builder()
                .username(userRegistrationDTO.getUsername())
                .email(userRegistrationDTO.getEmail())
                .password(userRegistrationDTO.getPassword())
                .build();
        userRepository.save(user);

        for (Currency currency : Currency.values()) {
            BigDecimal initBalance = currency == userRegistrationDTO.getCurrency() ? INIT_BALANCE : BigDecimal.valueOf(0);
            Balance balance = new Balance(currency, initBalance, user);
            balanceRepository.save(balance);
        }
    }
}
