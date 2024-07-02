package org.nacho.backend.services;

import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.exceptions.InvalidInput;

public interface ITransactionService {
    void newDeposit(SimpleTransactionDTO depositDTO) throws ResourceNotFound, InvalidInput;
    void newWithdraw(SimpleTransactionDTO withdrawDTO) throws ResourceNotFound, InvalidInput;
    void newTransfer(TransferDTO transferDTO) throws ResourceNotFound, InvalidInput;
    void newExchange(ExchangeDTO exchangeDTO) throws ResourceNotFound, InvalidInput;
}
