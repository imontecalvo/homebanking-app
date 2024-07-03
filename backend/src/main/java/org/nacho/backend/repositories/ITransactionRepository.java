package org.nacho.backend.repositories;

import org.nacho.backend.models.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);
}
