package org.nacho.backend.services;

import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;
import org.nacho.backend.repositories.IBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService implements IBalanceService{

    @Autowired
    private IBalanceRepository balanceRepository;
    @Override
    public List<BalanceDTO> getUserBalances(Long userId) throws ResourceNotFound {
        if (!balanceRepository.existsById(userId)){
            throw new ResourceNotFound("User does not exsit");
        }
        List<Balance> balances = balanceRepository.findAllByUserId(userId);
        return balances.stream().map(balance -> new BalanceDTO(balance.getCurrency(), balance.getAmount(), balance.getUser())).toList();
    }
}
