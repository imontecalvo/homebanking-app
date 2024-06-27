package org.nacho.backend.controllers;

import org.nacho.backend.dtos.UserRegistrationDTO;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("")
    public ResponseEntity<?> newUser(@RequestBody UserRegistrationDTO userRegistrationDTO) throws InvalidInput {
        userService.newUser(userRegistrationDTO);
        return ResponseEntity.status(201).body("User created successfully");
    }
}
