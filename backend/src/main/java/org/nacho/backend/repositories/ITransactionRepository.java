package org.nacho.backend.repositories;

import org.nacho.backend.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
}
