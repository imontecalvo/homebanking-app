package org.nacho.backend.services.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.UserEntity;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    @Mock
    private IBalanceRepository balanceRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private BalanceService balanceService;

    static MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    static SecurityContext securityContext;
    static Authentication authentication;
    static UserEntity userAuthenticated;
    @BeforeAll
    public static void setup(){
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        String username = "test_user";
        userAuthenticated = UserEntity.builder()
                .id(1L)
                .username(username)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userAuthenticated.getUsername());
    }

    @Test
    public void testGetUserBalancesWhenUserExists() throws ResourceNotFound {
        Balance balanceUSD = new Balance(Currency.USD, BigDecimal.valueOf(100), userAuthenticated);
        Balance balanceEUR = new Balance(Currency.EUR, BigDecimal.valueOf(200), userAuthenticated);
        Balance balanceARS = new Balance(Currency.ARS, BigDecimal.valueOf(300), userAuthenticated);
        List<Balance> balances = List.of(balanceUSD, balanceEUR, balanceARS);

        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).thenReturn(Optional.of(userAuthenticated));
        when(balanceRepository.findAllByUserId(userAuthenticated.getId())).thenReturn(balances);

        List<BalanceDTO> balancesResult =  balanceService.getUserBalances();

        List<BalanceDTO> expectedBalances = balances.stream()
                .map(balance -> new BalanceDTO(balance.getCurrency(), balance.getAmount(), balance.getUser()))
                .toList();
        assertEquals(3, balances.size());
        assertEquals(expectedBalances, balancesResult);
    }

    @Test
    public void testGetUserBalancesWhenUserDoesNotExists() throws ResourceNotFound {
        when(userRepository.findUserByUsername(userAuthenticated.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> balanceService.getUserBalances());
    }
}