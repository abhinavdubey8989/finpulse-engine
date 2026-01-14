package com.finpulse_engine.repository;

import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.entity.ExpenseTag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseTagRepository extends JpaRepository<ExpenseTag, UUID> {

    List<ExpenseTag> findByCategoryId(UUID categoryId);
    boolean existsByCategoryIdAndName(UUID categoryId, String name);
    ExpenseTag findByIdAndCategoryId(UUID id, UUID categoryId);
    boolean existsById(UUID id);
    List<ExpenseTag> findByCategoryIdIn(List<UUID> categoryIds);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ExpenseTag t
        SET t.name = :newName,
            t.updatedAt = CURRENT_TIMESTAMP
        WHERE t.id = :tagId
    """)
    int updateTagNameById(
            @Param("tagId") UUID tagId,
            @Param("newName") String newName
    );


}