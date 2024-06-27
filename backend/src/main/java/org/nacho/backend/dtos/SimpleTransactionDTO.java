package org.nacho.backend.dtos;

import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Data
public class SimpleTransactionDTO {
    private Currency currency;
    private BigDecimal amount;
}
