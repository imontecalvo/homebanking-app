package org.nacho.backend.models;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="transacciones")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tipo", nullable = false)
    private TransactionType type;

    @Column(name="moneda", nullable = false)
    private Currency currency;

    @Column(name="monto", nullable = false)
    private BigDecimal amount;

    @Column(name = "fecha", nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    User user;
}
