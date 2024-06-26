package org.nacho.backend.repositories;

import org.nacho.backend.models.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface ITransactionRepository extends CrudRepository<Transaction, Long> {
}
