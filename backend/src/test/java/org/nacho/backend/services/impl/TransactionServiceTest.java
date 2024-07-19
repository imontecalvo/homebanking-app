package org.nacho.backend.services.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.*;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.ITransactionRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private IBalanceRepository balanceRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private ITransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    static MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    static SecurityContext securityContext;
    static Authentication authentication;
    static UserEntity userAuthenticated;

    private Map<Currency, Balance> userBalances;

    @BeforeAll
    public static void setup(){
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String username = "test_user";
        userAuthenticated = UserEntity.builder()
                .id(1L)
                .username(username)
                .build();

        when(authentication.getPrincipal()).thenReturn(userAuthenticated.getUsername());
    }

    @BeforeEach
    public void setBalances(){
        BigDecimal INIT_BALANCE_AMOUNT = BigDecimal.valueOf(100.);

        userBalances = new HashMap<>();
        for (Currency currency: Currency.values()){
            Balance balance = new Balance(currency, INIT_BALANCE_AMOUNT, userAuthenticated);
            userBalances.put(currency, balance);
        }
    }

    @Test
    public void testNewDepositWhenSuccess() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO deposit = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(100.))
                .currency(Currency.USD)
                .build();

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .currency(deposit.getCurrency())
                .amount(deposit.getAmount())
                .user(userAuthenticated)
                .build();

        BigDecimal initBalanceAmount = userBalances.get(deposit.getCurrency()).getAmount();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(deposit.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(deposit.getCurrency())));

        //Act
        transactionService.newDeposit(deposit);

        //Assert
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionRepository).save(transactionArgumentCaptor.capture());

        ArgumentCaptor<Balance> balanceArgumentCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(any(Balance.class));
        verify(balanceRepository).save(balanceArgumentCaptor.capture());

        Transaction actualTransaction = transactionArgumentCaptor.getValue();
        assertEquals(transaction.getType(), actualTransaction.getType());
        assertEquals(transaction.getCurrency(), actualTransaction.getCurrency());
        assertEquals(transaction.getAmount(), actualTransaction.getAmount());
        assertEquals(transaction.getUser(), actualTransaction.getUser());

        Balance actualBalance = balanceArgumentCaptor.getValue();
        assertEquals(deposit.getCurrency(), actualBalance.getCurrency());
        assertEquals(initBalanceAmount.add(deposit.getAmount()), actualBalance.getAmount());
    }

    @Test
    public void testNewDepositWhenUserDoesNotExist() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO deposit = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(100.))
                .currency(Currency.USD)
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newDeposit(deposit));
    }

    @Test
    public void testNewDepositWhenInvalidCurrency() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO deposit = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(100.))
                .currency(Currency.USD)
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(deposit.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newDeposit(deposit));
    }

    @Test
    public void newWithdrawWhenSuccess() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(10.))
                .currency(Currency.USD)
                .build();

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .currency(withdraw.getCurrency())
                .amount(withdraw.getAmount())
                .user(userAuthenticated)
                .build();

        BigDecimal initBalanceAmount = userBalances.get(withdraw.getCurrency()).getAmount();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(withdraw.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(withdraw.getCurrency())));

        //Act
        transactionService.newWithdraw(withdraw);

        //Assert
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionRepository).save(transactionArgumentCaptor.capture());

        ArgumentCaptor<Balance> balanceArgumentCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(any(Balance.class));
        verify(balanceRepository).save(balanceArgumentCaptor.capture());

        Transaction actualTransaction = transactionArgumentCaptor.getValue();
        assertEquals(transaction.getType(), actualTransaction.getType());
        assertEquals(transaction.getCurrency(), actualTransaction.getCurrency());
        assertEquals(transaction.getAmount().multiply(BigDecimal.valueOf(-1)), actualTransaction.getAmount());
        assertEquals(transaction.getUser(), actualTransaction.getUser());

        Balance actualBalance = balanceArgumentCaptor.getValue();
        assertEquals(withdraw.getCurrency(), actualBalance.getCurrency());
        assertEquals(initBalanceAmount.subtract(withdraw.getAmount()), actualBalance.getAmount());
    }

    @Test
    public void testNewWithdrawWhenUserDoesNotExist() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(10.))
                .currency(Currency.USD)
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newWithdraw(withdraw));
    }

    @Test
    public void testNewWithdrawWhenInvalidCurrency() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(10.))
                .currency(Currency.USD)
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(withdraw.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newWithdraw(withdraw));
    }

    @Test
    public void testNewWithdrawWhenInsufficientBalance() throws ResourceNotFound, InvalidInput {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .amount(BigDecimal.valueOf(1000.))
                .currency(Currency.USD)
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(withdraw.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(withdraw.getCurrency())));

        //Act and Assert
        assertThrows(InvalidInput.class, () ->  transactionService.newWithdraw(withdraw));
    }
}
