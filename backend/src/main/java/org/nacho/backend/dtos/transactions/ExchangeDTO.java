package org.nacho.backend.dtos.transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Builder
@Data
public class ExchangeDTO {
    private Currency originCurrency;

    private Currency destinationCurrency;

    @NotNull
    @Positive
    private BigDecimal originAmount;
}