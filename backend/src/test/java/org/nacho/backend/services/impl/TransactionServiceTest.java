package org.nacho.backend.services.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
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
import java.util.HashMap;
import java.util.List;
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

    @Test
    public void testNewTransferWhenSuccess() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(80.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        BigDecimal destinationInitBalanceAmount = BigDecimal.valueOf(10.);
        BigDecimal originInitBalanceAmount = userBalances.get(transfer.getCurrency()).getAmount();
        Balance destinationBalance = new Balance(Currency.USD, destinationInitBalanceAmount, destinationUser);


        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(transfer.getCurrency())));
        when(userRepository.findUserByUsername(destinationUser.getUsername()))
                .thenReturn(Optional.of(destinationUser));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), destinationUser.getId()))
                .thenReturn(Optional.of(destinationBalance));

        //Act
        transactionService.newTransfer(transfer);

        //Assert
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(transactionArgumentCaptor.capture());

        ArgumentCaptor<Balance> balanceArgumentCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository, times(2)).save(balanceArgumentCaptor.capture());

        List<Transaction> allTransactionsSaved = transactionArgumentCaptor.getAllValues();
        assertEquals(TransactionType.TRANSFER, allTransactionsSaved.get(0).getType());
        assertEquals(transfer.getCurrency(), allTransactionsSaved.get(0).getCurrency());
        assertEquals(transfer.getAmount().multiply(BigDecimal.valueOf(-1)), allTransactionsSaved.get(0).getAmount());
        assertEquals(userAuthenticated, allTransactionsSaved.get(0).getUser());
        assertEquals(TransactionType.TRANSFER, allTransactionsSaved.get(1).getType());
        assertEquals(transfer.getCurrency(), allTransactionsSaved.get(1).getCurrency());
        assertEquals(transfer.getAmount(), allTransactionsSaved.get(1).getAmount());
        assertEquals(destinationUser, allTransactionsSaved.get(1).getUser());

        List<Balance> allBalancesSaved = balanceArgumentCaptor.getAllValues();
        assertEquals(transfer.getCurrency(), allBalancesSaved.get(0).getCurrency());
        assertEquals(originInitBalanceAmount.subtract(transfer.getAmount()), allBalancesSaved.get(0).getAmount());
        assertEquals(transfer.getCurrency(), allBalancesSaved.get(1).getCurrency());
        assertEquals(destinationInitBalanceAmount.add(transfer.getAmount()), allBalancesSaved.get(1).getAmount());
    }

    @Test
    public void testNewTransferWhenUserDoesNotExist() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(80.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newTransfer(transfer));
    }

    @Test
    public void testNewTransferWhenInvalidCurrency() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(80.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newTransfer(transfer));
    }

    @Test
    public void testNewTransferWhenDestinationUserDoesNotExist() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(80.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(transfer.getCurrency())));
        when(userRepository.findUserByUsername(destinationUser.getUsername()))
                .thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newTransfer(transfer));
    }

    @Test
    public void testNewTransferWhenInvalidDestinationCurrency() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(80.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(transfer.getCurrency())));
        when(userRepository.findUserByUsername(destinationUser.getUsername()))
                .thenReturn(Optional.of(destinationUser));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), destinationUser.getId()))
                .thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFound.class, () ->  transactionService.newTransfer(transfer));
    }

    @Test
    public void testNewTransferWhenInsufficientFunds() throws ResourceNotFound, InvalidInput {
        //Arrange
        UserEntity destinationUser = UserEntity.builder()
                .id(2L)
                .username("destination_user")
                .build();

        TransferDTO transfer = TransferDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(1000.))
                .destinationUsername(destinationUser.getUsername())
                .build();

        BigDecimal destinationInitBalanceAmount = BigDecimal.valueOf(10.);
        Balance destinationBalance = new Balance(Currency.USD, destinationInitBalanceAmount, destinationUser);


        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).
                thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), userAuthenticated.getId()))
                .thenReturn(Optional.of(userBalances.get(transfer.getCurrency())));
        when(userRepository.findUserByUsername(destinationUser.getUsername()))
                .thenReturn(Optional.of(destinationUser));
        when(balanceRepository.findBalanceByCurrencyAndUserId(transfer.getCurrency(), destinationUser.getId()))
                .thenReturn(Optional.of(destinationBalance));

        //Act and Assert
        assertThrows(InvalidInput.class, () ->  transactionService.newTransfer(transfer));
    }
}
