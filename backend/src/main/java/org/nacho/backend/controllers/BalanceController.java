package org.nacho.backend.controllers;

import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.services.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("denyAll()")
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private IBalanceService balanceService;

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping
    public ResponseEntity<?> getUserBalances() throws ResourceNotFound {
        List<BalanceDTO> balances = balanceService.getUserBalances();
        return ResponseEntity.status(200).body(balances);
    }
}
