package org.nacho.backend.controllers;

import org.nacho.backend.dtos.SimpleTransactionDTO;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction/")
public class TransactionController {
    @Autowired
    private ITransactionService transactionService;

    //FIXME: Obtener id usando token
    @PostMapping("deposit/{id}")
    public ResponseEntity<?> newDeposit(@RequestBody SimpleTransactionDTO depositDTO, @PathVariable Long id) throws ResourceNotFound {
        transactionService.newDeposit(depositDTO, id);
        return ResponseEntity.status(200).body("Deposit successfully completed");
    }

    //FIXME: Obtener id usando token
    @PostMapping("withdraw/{id}")
    public ResponseEntity<?> newWithdraw(@RequestBody SimpleTransactionDTO withdrawDTO, @PathVariable Long id) throws ResourceNotFound {
        transactionService.newWithdraw(withdrawDTO, id);
        return ResponseEntity.status(200).body("Withdraw successfully completed");
    }
}
