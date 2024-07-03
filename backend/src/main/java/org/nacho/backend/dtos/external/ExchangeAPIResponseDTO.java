package org.nacho.backend.dtos.external;

import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Data
public class ExchangeAPIResponseDTO {
    private String date;
    private Currency from;
    private Currency to;
    private BigDecimal amount;
    private BigDecimal value;
}
