package com.finpulse_engine.repository;

import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.enums.DifferentiatorType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {

    List<ExpenseCategory> findByUserId(UUID userId);

    boolean existsByUserIdAndCategory(UUID userId, String categoryName);
    boolean existsByUserIdAndId(UUID userId, UUID categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ExpenseCategory t
        SET t.category = :name,
            t.description = :description,
            t.monthlyUpperLimit = :monthlyUpperLimit,
            t.updatedAt = CURRENT_TIMESTAMP
        WHERE t.id = :categoryId
    """)
    int updateById(
            @Param("categoryId") UUID expenseCategoryId,
            @Param("name") String name,
            @Param("description") String description,
            @Param("monthlyUpperLimit") int monthlyUpperLimit
    );


    boolean existsByGroupIdAndCategory(UUID groupId, String categoryName);
    boolean existsByIdAndGroupId(UUID id, UUID groupId);

    List<ExpenseCategory> findByTypeAndGroupId(DifferentiatorType type, UUID groupId);
}