package org.nacho.backend.repositories;

import org.nacho.backend.models.Balance;
import org.springframework.data.repository.CrudRepository;

public interface IBalanceRepository extends CrudRepository<Balance, Long> {
}
