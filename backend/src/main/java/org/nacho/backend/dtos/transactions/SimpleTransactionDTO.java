package org.nacho.backend.dtos.transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Data
public class SimpleTransactionDTO {
    private Currency currency;

    @NotNull @Positive
    private BigDecimal amount;
}
