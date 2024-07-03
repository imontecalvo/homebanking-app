package org.nacho.backend.services.impl;

import jakarta.transaction.Transactional;
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
import org.nacho.backend.services.IUserService;
import org.nacho.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IBalanceRepository balanceRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final BigDecimal INIT_BALANCE = BigDecimal.valueOf(2000);

    @Transactional
    @Override
    public AuthResponse newUser(RegisterRequest registerRequest) throws InvalidInput {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new InvalidInput("Passwords do not match");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new InvalidInput("Username already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(getRolesFromEnums(registerRequest.getRoles()))
                .build();
        userRepository.save(user);

        for (Currency currency : Currency.values()) {
            BigDecimal initBalance = currency == registerRequest.getCurrency() ? INIT_BALANCE : BigDecimal.valueOf(0);
            Balance balance = new Balance(currency, initBalance, user);
            balanceRepository.save(balance);
        }

        String accessToken = createToken(registerRequest.getUsername(), registerRequest.getPassword());

        return AuthResponse.builder()
                .username(user.getUsername())
                .token(accessToken)
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String accessToken = createToken(loginRequest.getUsername(), loginRequest.getPassword());
        return AuthResponse.builder()
                .username(loginRequest.getUsername())
                .token(accessToken)
                .build();
    }

    private Set<Role> getRolesFromEnums(List<RoleEnum> enumRoles) {
        Set<Role> roles = new HashSet<>();

        enumRoles.forEach(role -> {
            Role userRole = null;
            try {
                userRole = roleRepository.findRoleByName(role)
                        .orElseThrow(() -> new InvalidInput("Role not found"));
            } catch (InvalidInput e) {
                throw new RuntimeException(e);
            }
            roles.add(userRole);
        });

        return roles;
    }

    private String createToken(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        return jwtUtils.createToken(authentication);
    }
}
