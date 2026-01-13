package com.finpulse_engine.repository;

import com.finpulse_engine.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    List<ExpenseCategory> findByUserId(UUID userId);

    boolean existsByUserIdAndCategory(UUID userId, String category);
}