package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.request.CreateExpenseCategoryRequest;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.repository.PersonalExpenseRepository;
import com.finpulse_engine.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {


    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    public CreateExpenseCategoryResponse createExpenseCategory(String userId, CreateExpenseCategoryRequest createExpenseCategoryRequest) {
        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndCategory(
                UUID.fromString(userId),
                createExpenseCategoryRequest.getCategory().trim().toLowerCase()
        );

        if(categoryExists) {
            throw new RuntimeException("Category already exists");
        }

        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .userId(UUID.fromString(userId))
                .category(createExpenseCategoryRequest.getCategory().trim().toLowerCase())
                .monthlyUpperLimit(createExpenseCategoryRequest.getMonthlyUpperLimit())
                .description(createExpenseCategoryRequest.getDescription())
                .build();

        ExpenseCategory saved = this.expenseCategoryRepository.save(expenseCategory);
        return new CreateExpenseCategoryResponse(saved.getId());
    }


    private ExpenseCategoryElement mapToResponse(ExpenseCategory expenseCategory) {
        return ExpenseCategoryElement.builder()
                .category(expenseCategory.getCategory())
                .monthlyUpperLimit(expenseCategory.getMonthlyUpperLimit())
                .description(expenseCategory.getDescription())
                .build();
    }


    public GetUserSettingsResponse getUserSettings(String userId) {

       List<ExpenseCategory> expenseCategories = this.expenseCategoryRepository.findByUserId(UUID.fromString(userId));
       return GetUserSettingsResponse.builder()
               .userId(userId)
               .userExpenseSetting(expenseCategories
                       .stream()
                       .map(this::mapToResponse)
                       .toList())
               .build();

    }



}
