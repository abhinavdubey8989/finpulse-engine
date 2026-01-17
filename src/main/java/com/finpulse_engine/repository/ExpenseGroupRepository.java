package com.finpulse_engine.repository;

import com.finpulse_engine.entity.ExpenseGroup;
import com.finpulse_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, UUID> {

    boolean existsByCreatedByAndName (UUID userId, String name);
}