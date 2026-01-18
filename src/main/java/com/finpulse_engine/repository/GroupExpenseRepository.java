package com.finpulse_engine.repository;

import com.finpulse_engine.entity.GroupExpense;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.enums.SplitType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface GroupExpenseRepository extends JpaRepository<GroupExpense, UUID>{

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE GroupExpense t
        SET t.categoryId = :categoryId,
            t.amount = :amount,
            t.description = :description,
            t.tagId = :tagId,
            t.splitType = :splitType,
            t.splits = :splits,
            t.dueAmount = :dueAmount,
            t.updatedAt = CURRENT_TIMESTAMP
        WHERE t.id = :id
    """)
    int updateById(
            @Param("id") UUID id,
            @Param("categoryId") UUID categoryId,
            @Param("amount") int amount,
            @Param("description") String description,
            @Param("tagId") UUID tagId,
            @Param("splitType") SplitType splitType,
            @Param("splits") Map<String, Integer> splits,
            @Param("dueAmount") Map<String, Integer> dueAmount
    );

}
