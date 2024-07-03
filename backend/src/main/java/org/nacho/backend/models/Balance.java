package org.nacho.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="balances", uniqueConstraints = {@UniqueConstraint(columnNames = {"moneda","id_usuario"})})
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="moneda", nullable = false)
    private Currency currency;

    @Column(name="monto", nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false)
    private UserEntity user;

    public Balance(Currency currency, BigDecimal amount, UserEntity user) {
        this.currency = currency;
        this.amount = amount;
        this.user = user;
    }
}
