package com.finpulse_engine.repository;

import com.finpulse_engine.entity.PersonalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalExpenseRepository extends JpaRepository<PersonalExpense, Long>{

    List<PersonalExpense> findByUserId(Long userId);

}
