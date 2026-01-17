package com.finpulse_engine.repository;

import com.finpulse_engine.entity.PersonalExpense;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalExpenseRepository extends JpaRepository<PersonalExpense, UUID>{

    List<PersonalExpense> findByUserId(UUID userId);
    List<PersonalExpense> findByUserIdAndYearAndMonth(UUID userId, Integer year, Integer month);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE PersonalExpense t
        SET t.categoryId = :categoryId,
            t.amount = :amount,
            t.description = :description,
            t.tagId = :tagId,
            t.updatedAt = CURRENT_TIMESTAMP
        WHERE t.id = :id
    """)
    int updateById(
            @Param("id") UUID id,
            @Param("categoryId") UUID categoryId,
            @Param("amount") int amount,
            @Param("description") String description,
            @Param("tagId") UUID tagId
    );

}
