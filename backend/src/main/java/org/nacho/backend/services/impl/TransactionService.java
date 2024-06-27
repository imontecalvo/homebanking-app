package org.nacho.backend.services.impl;

import jakarta.transaction.Transactional;
import org.nacho.backend.dtos.SimpleTransactionDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;
import org.nacho.backend.models.Transaction;
import org.nacho.backend.models.TransactionType;
import org.nacho.backend.models.User;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.ITransactionRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.nacho.backend.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IBalanceRepository balanceRepository;

    @Autowired
    private ITransactionRepository transactionRepository;

    @Override
    @Transactional
    public void newDeposit(SimpleTransactionDTO depositDTO, Long userId) throws ResourceNotFound {
        if (depositDTO.getAmount().compareTo(BigDecimal.ZERO)<=0){
            throw new ResourceNotFound("The amount must be greater than zero");
        }
        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(depositDTO.getCurrency(), userId);
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or currency");
        }

        Balance balance = balanceOptional.get();
        User user = userRepository.getReferenceById(userId);
        Transaction deposit = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .currency(depositDTO.getCurrency())
                .amount(depositDTO.getAmount())
                .date(new Date())
                .user(user)
                .build();
        balance.setAmount(balance.getAmount().add(depositDTO.getAmount()));

        transactionRepository.save(deposit);
        balanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void newWithdraw(SimpleTransactionDTO withdrawDTO, Long userId) throws ResourceNotFound {
        if (withdrawDTO.getAmount().compareTo(BigDecimal.ZERO) > 0){
            throw new ResourceNotFound("The amount must be greater than zero");
        }
        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(withdrawDTO.getCurrency(), userId);
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or currency");
        }

        Balance balance = balanceOptional.get();
        User user = userRepository.getReferenceById(userId);
        Transaction withdraw = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .currency(withdrawDTO.getCurrency())
                .amount(withdrawDTO.getAmount().multiply(BigDecimal.valueOf(-1)))
                .date(new Date())
                .user(user)
                .build();
        balance.setAmount(balance.getAmount().subtract(withdrawDTO.getAmount()));

        transactionRepository.save(withdraw);
        balanceRepository.save(balance);
    }

    @Override
    public void newTransfer(SimpleTransactionDTO transferDTO, Long userId) {

    }

    @Override
    public void newExchange(SimpleTransactionDTO exchangeDTO, Long userId) {

    }
}
