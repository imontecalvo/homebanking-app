package org.nacho.backend.dtos.transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;

import java.math.BigDecimal;

@Builder
@Data
public class TransferDTO {
    private Currency currency;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @NotBlank
    private String destinationUsername;
}