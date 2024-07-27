package org.nacho.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.UserEntity;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@Data
public class BalanceDTO {
    private Currency currency;
    private BigDecimal amount;
    private Long userId;

    public BalanceDTO(Currency currency, BigDecimal amount, UserEntity user) {
        this.currency = currency;
        this.amount = amount;
        this.userId = user.getId();
    }
}
