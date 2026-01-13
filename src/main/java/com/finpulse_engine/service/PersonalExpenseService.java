package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.response.CreatePersonalExpenseResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseResponse;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.repository.PersonalExpenseRepository;
import com.finpulse_engine.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PersonalExpenseService {

    @Autowired
    private PersonalExpenseRepository personalExpenseRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;


    public CreatePersonalExpenseResponse createExpense(CreatePersonalExpenseRequest request) {

        PersonalExpense expense = PersonalExpense.builder()
                .userId(UUID.fromString(request.getUserId()))
                .year(request.getYear())
                .month(request.getMonth())
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();


        PersonalExpense saved = this.personalExpenseRepository.save(expense);
        return new CreatePersonalExpenseResponse(saved.getId());
    }

    private GetPersonalExpenseResponse mapToResponse(PersonalExpense expense) {
        return GetPersonalExpenseResponse.builder()
                .id(expense.getId())
                .year(expense.getYear())
                .month(expense.getMonth())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }


    public List<GetPersonalExpenseResponse> getAllPersonalExpenses(String userId) {
        return this.personalExpenseRepository.findByUserId(UUID.fromString(userId))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

}
