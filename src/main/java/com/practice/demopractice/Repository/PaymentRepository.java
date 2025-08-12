package com.practice.demopractice.Repository;

import com.practice.demopractice.entity.Payment;
import com.practice.demopractice.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    @EntityGraph(attributePaths = {"user", "recipient"})
    @Override
    Page<Payment> findAll(Specification<Payment> spec, Pageable pageable);

    List<Payment> findByUser_IdAndTransactionType(Long userId, TransactionType type);
}