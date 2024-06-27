package org.nacho.backend.services.impl;

import jakarta.transaction.Transactional;
import org.nacho.backend.dtos.external.ExchangeAPIResponseDTO;
import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.*;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.ITransactionRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.nacho.backend.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    @Autowired
    private RestTemplate restTemplate;

    private static final BigDecimal EXCHANGE_FEE_RATE = BigDecimal.valueOf(0.01);

    //FIXME: Refactorizar checks -> codigo repetido
    @Override
    @Transactional
    public void newDeposit(SimpleTransactionDTO depositDTO, Long userId) throws ResourceNotFound, InvalidInput {
        if (depositDTO.getAmount().compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidInput("The amount must be greater than zero.");
        }
        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(depositDTO.getCurrency(), userId);
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or currency.");
        }

        Balance balance = balanceOptional.get();
        User user = userRepository.getReferenceById(userId);
        registerTransaction(TransactionType.DEPOSIT, user, balance, depositDTO.getAmount());
    }

    @Override
    @Transactional
    public void newWithdraw(SimpleTransactionDTO withdrawDTO, Long userId) throws ResourceNotFound, InvalidInput {
        if (withdrawDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInput("The amount must be greater than zero.");
        }
        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(withdrawDTO.getCurrency(), userId);
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or currency.");
        }

        Balance balance = balanceOptional.get();
        if (balance.getAmount().compareTo(withdrawDTO.getAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }
        User user = userRepository.getReferenceById(userId);
        BigDecimal finalAmount = withdrawDTO.getAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.WITHDRAW, user, balance, finalAmount);
    }

    @Override
    @Transactional
    public void newTransfer(TransferDTO transferDTO, Long userId) throws ResourceNotFound, InvalidInput {
        if (transferDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInput("The amount must be greater than zero.");
        }
        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(transferDTO.getCurrency(), userId);
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid origin user or currency.");
        }
        Optional<User> destinationUserOptional = userRepository.findUserByUsername(transferDTO.getDestinationUsername());
        if (destinationUserOptional.isEmpty()) {
            throw new ResourceNotFound("Destination user does not exist.");
        }
        User destinationUser = destinationUserOptional.get();
        Optional<Balance> destinationBalanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(transferDTO.getCurrency(), destinationUser.getId());
        if (destinationBalanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid destination user or currency.");
        }

        //Cuenta origen
        Balance originBalance = balanceOptional.get();
        if (originBalance.getAmount().compareTo(transferDTO.getAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }
        User originUser = userRepository.getReferenceById(userId);
        BigDecimal finalOriginAmount = transferDTO.getAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.TRANSFER, originUser, originBalance, finalOriginAmount);

        //Cuenta destino
        Balance destionationBalance = destinationBalanceOptional.get();
        registerTransaction(TransactionType.TRANSFER, destinationUser, destionationBalance, transferDTO.getAmount());
    }

    @Override
    @Transactional
    public void newExchange(ExchangeDTO exchangeDTO, Long userId) throws ResourceNotFound, InvalidInput {
        if (exchangeDTO.getOriginAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInput("The amount must be greater than zero.");
        }
        Optional<Balance> originBalanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(exchangeDTO.getOriginCurrency(), userId);
        if (originBalanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or origin currency.");
        }
        Optional<Balance> destinationBalanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(exchangeDTO.getDestinationCurrency(), userId);
        if (destinationBalanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid user or destination currency.");
        }
        Balance originBalance = originBalanceOptional.get();
        if (originBalance.getAmount().compareTo(exchangeDTO.getOriginAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }

        //Debitar en moneda origen
        User user = userRepository.getReferenceById(userId);
        BigDecimal finalOriginAmount = exchangeDTO.getOriginAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.EXCHANGE, user, originBalance, finalOriginAmount);

        //Acreditar en moneda destino
        Balance destinationBalance = destinationBalanceOptional.get();
        BigDecimal destinationAmount = convert(exchangeDTO.getOriginCurrency(), exchangeDTO.getDestinationCurrency(), exchangeDTO.getOriginAmount());
        BigDecimal finalDestinationAmount = destinationAmount.subtract(destinationAmount.multiply(EXCHANGE_FEE_RATE));
        registerTransaction(TransactionType.EXCHANGE, user, destinationBalance, finalDestinationAmount);
    }

    private void registerTransaction(TransactionType type, User user, Balance balance, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .type(type)
                .currency(balance.getCurrency())
                .amount(amount)
                .date(new Date())
                .user(user)
                .build();
        balance.setAmount(balance.getAmount().add(amount));

        transactionRepository.save(transaction);
        balanceRepository.save(balance);
    }

    private BigDecimal convert(Currency from, Currency to, BigDecimal amountToConvert) {
        String url = String.format("https://api.currencybeacon.com/v1/convert?api_key=lPypUnrIchpi40Vo4BjZXNjsTubIR9K2&from=%s&to=%s&amount=%f", from.name(), to.name(), amountToConvert);
        ExchangeAPIResponseDTO response = restTemplate.getForObject(url, ExchangeAPIResponseDTO.class);
        assert response != null;
        return response.getValue();
    }
}
