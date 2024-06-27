package org.nacho.backend.services;

import org.nacho.backend.dtos.SimpleTransactionDTO;
import org.nacho.backend.exceptions.ResourceNotFound;

public interface ITransactionService {
    void newDeposit(SimpleTransactionDTO depositDTO, Long userId) throws ResourceNotFound;
    void newWithdraw(SimpleTransactionDTO withdrawDTO, Long userId) throws ResourceNotFound;
    void newTransfer(SimpleTransactionDTO transferDTO, Long userId);
    void newExchange(SimpleTransactionDTO exchangeDTO, Long userId);
}
