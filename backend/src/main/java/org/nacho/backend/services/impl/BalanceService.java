package org.nacho.backend.services.impl;

import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;
import org.nacho.backend.models.UserEntity;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.nacho.backend.services.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService implements IBalanceService {

    @Autowired
    private IBalanceRepository balanceRepository;

    @Autowired
    private IUserRepository userRepository;
    @Override
    public List<BalanceDTO> getUserBalances() throws ResourceNotFound {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        List<Balance> balances = balanceRepository.findAllByUserId(user.getId());
        return balances.stream().map(balance -> new BalanceDTO(balance.getCurrency(), balance.getAmount(), balance.getUser())).toList();
    }
}
