package org.nacho.backend.services;

import org.nacho.backend.dtos.TransactionDTO;
import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {
    void newDeposit(SimpleTransactionDTO depositDTO) throws ResourceNotFound, InvalidInput;
    void newWithdraw(SimpleTransactionDTO withdrawDTO) throws ResourceNotFound, InvalidInput;
    void newTransfer(TransferDTO transferDTO) throws ResourceNotFound, InvalidInput;
    void newExchange(ExchangeDTO exchangeDTO) throws ResourceNotFound, InvalidInput;

    List<TransactionDTO> getTransactionHistory(Integer page, Integer items) throws InvalidInput, ResourceNotFound;

    Long getNumberOfTransactions() throws ResourceNotFound;

    BigDecimal convertCurrency(Currency from, Currency to, BigDecimal amount) throws ResourceNotFound;
}