package org.nacho.backend.controllers;

import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.InvalidInput;
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
    public ResponseEntity<?> newDeposit(@RequestBody SimpleTransactionDTO depositDTO, @PathVariable Long id) throws ResourceNotFound, InvalidInput {
        transactionService.newDeposit(depositDTO, id);
        return ResponseEntity.status(200).body("Deposit successfully completed");
    }

    //FIXME: Obtener id usando token
    @PostMapping("withdraw/{id}")
    public ResponseEntity<?> newWithdraw(@RequestBody SimpleTransactionDTO withdrawDTO, @PathVariable Long id) throws ResourceNotFound, InvalidInput {
        transactionService.newWithdraw(withdrawDTO, id);
        return ResponseEntity.status(200).body("Withdraw successfully completed");
    }

    //FIXME: Obtener id usando token
    @PostMapping("transfer/{id}")
    public ResponseEntity<?> newTransfer(@RequestBody TransferDTO transferDTO, @PathVariable Long id) throws ResourceNotFound, InvalidInput {
        transactionService.newTransfer(transferDTO, id);
        return ResponseEntity.status(200).body("Transfer successfully completed");
    }

    //FIXME: Obtener id usando token
    @PostMapping("exchange/{id}")
    public ResponseEntity<?> newExchange(@RequestBody ExchangeDTO exchangeDTO, @PathVariable Long id) throws ResourceNotFound, InvalidInput {
        transactionService.newExchange(exchangeDTO, id);
        return ResponseEntity.status(200).body("Exchange successfully completed");
    }
}
