package org.nacho.backend.services;

import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;

import java.util.List;

public interface IBalanceService {
    List<BalanceDTO> getUserBalances() throws ResourceNotFound;
}
