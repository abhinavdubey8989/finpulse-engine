package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.request.GetPersonalExpenseSumaryRequest;
import com.finpulse_engine.dto.response.CreatePersonalExpenseResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseSumaryResponse;
import com.finpulse_engine.dto.response.PersonalExpenseSumaryElement;
import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.repository.PersonalExpenseRepository;
import com.finpulse_engine.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonalExpenseService {

    @Autowired
    private PersonalExpenseRepository personalExpenseRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;


    public CreatePersonalExpenseResponse createExpense(CreatePersonalExpenseRequest request) {

        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndId(
                UUID.fromString(request.getUserId()),
                UUID.fromString(request.getCategoryId())
        );

        if (!categoryExists) {
            throw new RuntimeException("Category Not Found");
        }

        PersonalExpense expense = PersonalExpense.builder()
                .userId(UUID.fromString(request.getUserId()))
                .year(request.getYear())
                .month(request.getMonth())
                .categoryId(UUID.fromString(request.getCategoryId()))
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();


        PersonalExpense saved = this.personalExpenseRepository.save(expense);
        return new CreatePersonalExpenseResponse(saved.getId().toString());
    }

    private GetPersonalExpenseResponse mapToResponse(PersonalExpense expense) {
        return GetPersonalExpenseResponse.builder()
                .id(expense.getId().toString())
                .year(expense.getYear())
                .month(expense.getMonth())
                .category(expense.getCategoryId().toString())
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


    public GetPersonalExpenseSumaryResponse getPersonalExpenseSummary(String userId, GetPersonalExpenseSumaryRequest getPersonalExpenseSumaryRequest) {
        List<PersonalExpense> expenses = this.personalExpenseRepository.findByUserIdAndYearAndMonth(
                UUID.fromString(userId),
                getPersonalExpenseSumaryRequest.getYear(),
                getPersonalExpenseSumaryRequest.getMonth()
        );

        List<ExpenseCategory> categories = this.expenseCategoryRepository.findByUserId(UUID.fromString(userId));
        // Index categories by ID for fast lookup
        Map<UUID, ExpenseCategory> categoryMap =
                categories.stream()
                        .collect(Collectors.toMap(
                                ExpenseCategory::getId,
                                Function.identity()
                        ));

        // Group expenses by categoryId and sum amounts
        Map<UUID, Integer> expenseSumByCategory =
                expenses.stream()
                        .collect(Collectors.groupingBy(
                                PersonalExpense::getCategoryId,
                                Collectors.summingInt(PersonalExpense::getAmount)
                        ));

        // Build summary elements
        List<PersonalExpenseSumaryElement> elements = expenseSumByCategory.entrySet()
                .stream()
                .map(entry -> {
                    UUID categoryId = entry.getKey();
                    Integer totalSpent = entry.getValue();
                    ExpenseCategory category =
                            categoryMap.get(categoryId);
                    return PersonalExpenseSumaryElement.builder()
                            .categoryId(categoryId.toString())
                            .category(category.getCategory())
                            .categoryDescription(category.getDescription())
                            .monthlyUpperLimit(
                                    category.getMonthlyUpperLimit().longValue()
                            )
                            .monthlyExpenseDone(totalSpent.longValue())
                            .build();
                })
                .toList();

        int totalExpenseAmount = expenses.stream().mapToInt(expense -> expense.getAmount()).sum();

        return GetPersonalExpenseSumaryResponse.builder()
                .userId(userId)
                .year(getPersonalExpenseSumaryRequest.getYear())
                .month(getPersonalExpenseSumaryRequest.getMonth())
                .numberOfExpenses(expenses.size())
                .totalExpenseAmount(totalExpenseAmount)
                .elements(elements)
                .build();
    }

}
