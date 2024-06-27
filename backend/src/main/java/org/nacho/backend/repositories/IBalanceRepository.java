package org.nacho.backend.repositories;

import org.nacho.backend.models.Balance;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IBalanceRepository extends CrudRepository<Balance, Long> {
    List<Balance> findAllByUserId(Long userId);
}
