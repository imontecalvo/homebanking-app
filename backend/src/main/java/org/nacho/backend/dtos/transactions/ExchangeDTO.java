package org.nacho.backend.dtos.transactions;

import lombok.Data;
import org.nacho.backend.models.Currency;
import java.math.BigDecimal;

@Data
public class ExchangeDTO {
    private Currency originCurrency;
    private Currency destinationCurrency;
    private BigDecimal originAmount;
}