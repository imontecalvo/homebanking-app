package org.nacho.backend.dtos;

import jakarta.persistence.*;
import lombok.Data;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.User;

import java.math.BigDecimal;

@Data
public class BalanceDTO {
    private Currency currency;
    private BigDecimal amount;
    private Long userId;

    public BalanceDTO(Currency currency, BigDecimal amount, User user) {
        this.currency = currency;
        this.amount = amount;
        this.userId = user.getId();
    }
}
