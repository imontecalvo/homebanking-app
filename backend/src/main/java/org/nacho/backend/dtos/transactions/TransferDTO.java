package org.nacho.backend.dtos.transactions;

import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Data
public class TransferDTO {
    private Currency currency;
    private BigDecimal amount;
    private String destinationUsername;
}