package org.nacho.backend.controllers;

import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("denyAll()")
@RequestMapping("/api/transaction/")
public class TransactionController {
    @Autowired
    private ITransactionService transactionService;

    //FIXME: Obtener id usando token
    @PreAuthorize("hasRole('USER')")
    @PostMapping("deposit")
    public ResponseEntity<?> newDeposit(@RequestBody SimpleTransactionDTO depositDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newDeposit(depositDTO);
        return ResponseEntity.status(200).body("Deposit successfully completed");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("withdraw")
    public ResponseEntity<?> newWithdraw(@RequestBody SimpleTransactionDTO withdrawDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newWithdraw(withdrawDTO);
        return ResponseEntity.status(200).body("Withdraw successfully completed");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("transfer")
    public ResponseEntity<?> newTransfer(@RequestBody TransferDTO transferDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newTransfer(transferDTO);
        return ResponseEntity.status(200).body("Transfer successfully completed");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("exchange")
    public ResponseEntity<?> newExchange(@RequestBody ExchangeDTO exchangeDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newExchange(exchangeDTO);
        return ResponseEntity.status(200).body("Exchange successfully completed");
    }
}
