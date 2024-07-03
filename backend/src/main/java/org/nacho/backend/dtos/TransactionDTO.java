package org.nacho.backend.dtos;

import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class TransactionDTO {
    private TransactionType type;
    private Currency currency;
    private BigDecimal amount;
    private Date date;
    String username;
}
