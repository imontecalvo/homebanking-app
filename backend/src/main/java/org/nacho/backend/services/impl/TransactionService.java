package org.nacho.backend.services.impl;

import jakarta.transaction.Transactional;
import org.nacho.backend.dtos.TransactionDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public BigDecimal convertCurrency(Currency from, Currency to, BigDecimal amount) throws ResourceNotFound {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userRepository.existsByUsername(username)){
            throw new ResourceNotFound("The user does not exist.");
        }
        return convert(from, to, amount);
    }

    public Long getNumberOfTransactions() throws ResourceNotFound {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));
        return transactionRepository.countByUserId(user.getId());
    }
    @Override
    public List<TransactionDTO> getTransactionHistory(Integer page, Integer items) throws InvalidInput, ResourceNotFound {
        if (page < 0 || items < 0) {
            throw new InvalidInput("Invalid page or items.");
        }

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        if (items == 0){
            return new ArrayList<TransactionDTO>();
        }

        Pageable pageable = PageRequest.of(page, items, Sort.by(Sort.Direction.DESC, "date"));
        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable);
        return transactions.stream()
                .map(transaction -> TransactionDTO.builder()
                        .type(transaction.getType())
                        .currency(transaction.getCurrency())
                        .amount(transaction.getAmount())
                        .date(transaction.getDate())
                        .username(user.getUsername())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void newDeposit(SimpleTransactionDTO depositDTO) throws ResourceNotFound, InvalidInput {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        Optional<Balance> balanceOptional = balanceRepository.findBalanceByCurrencyAndUserId(depositDTO.getCurrency(), user.getId());
        if (balanceOptional.isEmpty()) {
            throw new ResourceNotFound("Invalid currency.");
        }

        Balance balance = balanceOptional.get();
        registerTransaction(TransactionType.DEPOSIT, user, balance, depositDTO.getAmount());
    }

    @Override
    @Transactional
    public void newWithdraw(SimpleTransactionDTO withdrawDTO) throws ResourceNotFound, InvalidInput {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        Balance balance = balanceRepository
                .findBalanceByCurrencyAndUserId(withdrawDTO.getCurrency(), user.getId())
                .orElseThrow(()-> new ResourceNotFound("Invalid currency."));

        if (balance.getAmount().compareTo(withdrawDTO.getAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }

        BigDecimal finalAmount = withdrawDTO.getAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.WITHDRAW, user, balance, finalAmount);
    }

    @Override
    @Transactional
    public void newTransfer(TransferDTO transferDTO) throws ResourceNotFound, InvalidInput {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity originUser = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        Balance originBalance = balanceRepository
                .findBalanceByCurrencyAndUserId(transferDTO.getCurrency(), originUser.getId())
                .orElseThrow(()-> new ResourceNotFound("Invalid currency."));

        UserEntity destinationUser = userRepository
                .findUserByUsername(transferDTO.getDestinationUsername())
                .orElseThrow(()-> new ResourceNotFound("Destination user does not exist."));

        Balance destinationBalance = balanceRepository
                .findBalanceByCurrencyAndUserId(transferDTO.getCurrency(), destinationUser.getId())
                .orElseThrow(()->new ResourceNotFound("Invalid destination user or currency."));

        //Cuenta origen
        if (originBalance.getAmount().compareTo(transferDTO.getAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }
        BigDecimal finalOriginAmount = transferDTO.getAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.TRANSFER, originUser, originBalance, finalOriginAmount);

        //Cuenta destino
        registerTransaction(TransactionType.TRANSFER, destinationUser, destinationBalance, transferDTO.getAmount());
    }

    @Override
    @Transactional
    public void newExchange(ExchangeDTO exchangeDTO) throws ResourceNotFound, InvalidInput {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("The user does not exist."));

        Balance originBalance = balanceRepository
                .findBalanceByCurrencyAndUserId(exchangeDTO.getOriginCurrency(), user.getId())
                .orElseThrow(() -> new ResourceNotFound("Invalid user or origin currency."));

        Balance destinationBalance = balanceRepository
                .findBalanceByCurrencyAndUserId(exchangeDTO.getDestinationCurrency(), user.getId())
                .orElseThrow(()->new ResourceNotFound("Invalid user or destination currency."));

        if (originBalance.getAmount().compareTo(exchangeDTO.getOriginAmount()) < 0) {
            throw new InvalidInput("Insufficient funds.");
        }

        //Debitar en moneda origen
        BigDecimal finalOriginAmount = exchangeDTO.getOriginAmount().multiply(BigDecimal.valueOf(-1));
        registerTransaction(TransactionType.EXCHANGE, user, originBalance, finalOriginAmount);

        //Acreditar en moneda destino
        BigDecimal destinationAmount = convert(exchangeDTO.getOriginCurrency(), exchangeDTO.getDestinationCurrency(), exchangeDTO.getOriginAmount());
        BigDecimal finalDestinationAmount = destinationAmount.subtract(destinationAmount.multiply(EXCHANGE_FEE_RATE));
        registerTransaction(TransactionType.EXCHANGE, user, destinationBalance, finalDestinationAmount);
    }

    private void registerTransaction(TransactionType type, UserEntity user, Balance balance, BigDecimal amount) {
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
