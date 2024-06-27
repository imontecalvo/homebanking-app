package org.nacho.backend.controllers;

import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Balance;
import org.nacho.backend.repositories.IBalanceRepository;
import org.nacho.backend.services.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/{id}/balance")
public class BalanceController {

    @Autowired
    private IBalanceService balanceService;
    @GetMapping
    public ResponseEntity<?> getUserBalances(@PathVariable Long id) throws ResourceNotFound {
        List<BalanceDTO> balances = balanceService.getUserBalances(id);
        return ResponseEntity.status(200).body(balances);
    }
}
