package org.nacho.backend.dtos.external;

import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;

import java.math.BigDecimal;

@Builder
@Data
public class ExchangeAPIResponseDTO {
    private String date;
    private Currency from;
    private Currency to;
    private BigDecimal amount;
    private BigDecimal value;
}
