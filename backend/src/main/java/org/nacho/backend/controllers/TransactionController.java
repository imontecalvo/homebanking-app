package org.nacho.backend.controllers;

import jakarta.validation.Valid;
import org.nacho.backend.dtos.SuccessIntegerResponseDTO;
import org.nacho.backend.dtos.SuccessStringResponseDTO;
import org.nacho.backend.dtos.TransactionDTO;
import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.exceptions.InvalidInput;
import org.nacho.backend.exceptions.ResourceNotFound;
import org.nacho.backend.models.Currency;
import org.nacho.backend.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("denyAll()")
@RequestMapping("/api/transaction/")
public class TransactionController {
    @Autowired
    private ITransactionService transactionService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("deposit")
    public ResponseEntity<?> newDeposit(@RequestBody @Valid SimpleTransactionDTO depositDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newDeposit(depositDTO);
        return ResponseEntity.status(200).body(new SuccessStringResponseDTO("Deposit successfully completed"));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("withdraw")
    public ResponseEntity<?> newWithdraw(@RequestBody @Valid SimpleTransactionDTO withdrawDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newWithdraw(withdrawDTO);
        return ResponseEntity.status(200).body(new SuccessStringResponseDTO("Withdraw successfully completed"));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("transfer")
    public ResponseEntity<?> newTransfer(@RequestBody @Valid TransferDTO transferDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newTransfer(transferDTO);
        return ResponseEntity.status(200).body(new SuccessStringResponseDTO("Transfer successfully completed"));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("exchange")
    public ResponseEntity<?> newExchange(@RequestBody @Valid ExchangeDTO exchangeDTO) throws ResourceNotFound, InvalidInput {
        transactionService.newExchange(exchangeDTO);
        return ResponseEntity.status(200).body(new SuccessStringResponseDTO("Exchange successfully completed"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("history")
    public ResponseEntity<?> getTransactionHistory(@RequestParam Integer page, @RequestParam Integer items ) throws ResourceNotFound, InvalidInput {
        List<TransactionDTO> transactions = transactionService.getTransactionHistory(page, items);
        return ResponseEntity.status(200).body(transactions);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("history-size")
    public ResponseEntity<?> getNumberOfTransactions() throws ResourceNotFound, InvalidInput {
        Long numberOfTransactions = transactionService.getNumberOfTransactions();
        return ResponseEntity.status(200).body(new SuccessIntegerResponseDTO(numberOfTransactions.doubleValue()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam Currency from,
            @RequestParam Currency to,
            @RequestParam BigDecimal amount
    ) throws ResourceNotFound, InvalidInput {
        BigDecimal conversion = transactionService.convertCurrency(from, to, amount);
        return ResponseEntity.status(200).body(new SuccessIntegerResponseDTO(conversion.doubleValue()));
    }
}
