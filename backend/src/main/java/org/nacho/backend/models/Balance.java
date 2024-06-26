package org.nacho.backend.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="balances", uniqueConstraints = {@UniqueConstraint(columnNames = {"moneda","id_usuario"})})
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="moneda", nullable = false)
    private Currency currency;

    @Column(name="monto", nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false)
    private User user;
}
