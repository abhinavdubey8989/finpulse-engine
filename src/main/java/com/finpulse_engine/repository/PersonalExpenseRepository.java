package com.finpulse_engine.repository;

import com.finpulse_engine.entity.PersonalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalExpenseRepository extends JpaRepository<PersonalExpense, UUID>{

    List<PersonalExpense> findByUserId(UUID userId);
    List<PersonalExpense> findByUserIdAndYearAndMonth(UUID userId, Integer year, Integer month);


}
