package org.nacho.backend.controllers;

import org.nacho.backend.dtos.auth.AuthResponse;
import org.nacho.backend.dtos.auth.LoginRequest;
import org.nacho.backend.dtos.auth.RegisterRequest;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("denyAll()")
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    private IUserService userService;

    @PreAuthorize("permitAll()")
    @PostMapping("register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest registerRequest) throws InvalidInput {
        AuthResponse response = userService.newUser(registerRequest);
        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) throws InvalidInput {
        AuthResponse response = userService.login(loginRequest);
        return ResponseEntity.status(200).body(response);
    }
}
