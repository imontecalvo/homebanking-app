package org.nacho.backend.repositories;

import org.nacho.backend.models.Balance;
import org.nacho.backend.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IBalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findAllByUserId(Long userId);

    Optional<Balance> findBalanceByCurrencyAndUserId(Currency currency, Long userId);
    boolean existsBalanceByUserIdAndCurrency(Long userId, Currency currency);
}
