package org.nacho.backend.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.auth.AuthResponse;
import org.nacho.backend.dtos.auth.LoginRequest;
import org.nacho.backend.dtos.auth.RegisterRequest;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.models.Balance;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.UserEntity;
import org.nacho.backend.models.roles_authorities.Role;
import org.nacho.backend.models.roles_authorities.RoleEnum;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.repositories.IRoleRepository;
import org.nacho.backend.repositories.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Spy
    @InjectMocks
    UserService userService;

    @Mock
    IUserRepository userRepository;
    @Mock
    IRoleRepository roleRepository;
    @Mock
    IBalanceRepository balanceRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Test
    public void testNewUserWhenSuccess() throws InvalidInput {
        //Arrange
        RegisterRequest newUserRequest = RegisterRequest.builder()
                .username("test_user")
                .password("test_password")
                .confirmPassword("test_password")
                .currency(Currency.USD)
                .roles(List.of(RoleEnum.USER))
                .build();

        Role role = Role.builder()
                .name(newUserRequest.getRoles().get(0))
                .build();

        when(roleRepository.findRoleByName(newUserRequest.getRoles().get(0))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(newUserRequest.getPassword())).thenReturn(newUserRequest.getPassword());
        doReturn("test_token").when(userService).createToken(newUserRequest.getUsername(), newUserRequest.getPassword());

        //Act
        AuthResponse response = userService.newUser(newUserRequest);

        //Assert
        ArgumentCaptor<UserEntity> userArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        UserEntity userSaved = userArgumentCaptor.getValue();
        assertEquals(newUserRequest.getUsername(), userSaved.getUsername());
        assertEquals(newUserRequest.getPassword(), userSaved.getPassword());
        assertEquals(1, userSaved.getRoles().size());
        assertEquals(Set.of(role), userSaved.getRoles());

        ArgumentCaptor<Balance> balanceArgumentCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository, times(Currency.values().length)).save(balanceArgumentCaptor.capture());
        List<Balance> allBalances = balanceArgumentCaptor.getAllValues();
        List<Currency> remainCurrencies = new ArrayList<>(Arrays.stream(Currency.values()).toList());
        for (Balance balance : allBalances){
            BigDecimal expectedAmount = balance.getCurrency().equals(newUserRequest.getCurrency()) ?
                    userService.INIT_BALANCE : BigDecimal.ZERO;
            assertEquals(expectedAmount, balance.getAmount());
            assertTrue(remainCurrencies.contains(balance.getCurrency()));
            remainCurrencies.remove(balance.getCurrency());
            assertEquals(userSaved, balance.getUser());
        }

        assertEquals(newUserRequest.getUsername(), response.getUsername());
        assertEquals("test_token", response.getToken());
    }

    @Test
    public void testNewUserWhenPasswordsDoNotMatch(){
        //Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("test_user")
                .password("test_password")
                .confirmPassword("test_password2")
                .currency(Currency.USD)
                .roles(List.of(RoleEnum.USER))
                .build();

        //Act & Assert
        assertThrows(InvalidInput.class, () -> userService.newUser(request));
    }

    @Test
    public void testNewUserWhenUserDoesNotExist(){
        //Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("test_user")
                .password("test_password")
                .confirmPassword("test_password")
                .currency(Currency.USD)
                .roles(List.of(RoleEnum.USER))
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        //Act & Assert
        assertThrows(InvalidInput.class, () -> userService.newUser(request));
    }

    @Test
    public void testNewUserWhenRoleDoesNotExist(){
        //Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("test_user")
                .password("test_password")
                .confirmPassword("test_password")
                .currency(Currency.USD)
                .roles(List.of(RoleEnum.USER))
                .build();

        when(roleRepository.findRoleByName(request.getRoles().get(0))).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> userService.newUser(request));
    }

    @Test
    public void testLoginWhenSuccess(){
        //Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .username("test_user")
                .password("test_password")
                .build();
        String expectedToken = "test_token";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        doReturn(expectedToken).when(userService).createToken(loginRequest.getUsername(), loginRequest.getPassword());

        //Act
        AuthResponse response = userService.login(loginRequest);

        //Assert
        assertEquals(loginRequest.getUsername(), response.getUsername());
        assertEquals(expectedToken, response.getToken());
    }

    @Test
    public void testLoginWhenAuthenticationFails(){
        //Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .username("test_user")
                .password("test_password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        //Act & Assert
        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
    }
}
