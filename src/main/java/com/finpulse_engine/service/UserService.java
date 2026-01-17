package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.request.CreateExpenseCategoryRequest;
import com.finpulse_engine.dto.request.UpdateExpenseCategoryRequest;
import com.finpulse_engine.dto.request.UpdateExpenseTagRequest;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.entity.ExpenseTag;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.enums.DifferentiatorType;
import com.finpulse_engine.repository.ExpenseTagRepository;
import com.finpulse_engine.repository.PersonalExpenseRepository;
import com.finpulse_engine.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {


    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private ExpenseTagRepository expenseTagRepository;

    @Autowired
    private TagService tagService;


    public CreateExpenseCategoryResponse createExpenseCategory(String userId, CreateExpenseCategoryRequest createExpenseCategoryRequest) {
        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndCategory(
                UUID.fromString(userId),
                createExpenseCategoryRequest.getCategoryName().trim().toLowerCase()
        );

        if (categoryExists) {
            throw new RuntimeException("Category already exists");
        }

        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .userId(UUID.fromString(userId))
                .type(DifferentiatorType.PERSONAL)
                .groupId(null)
                .category(createExpenseCategoryRequest.getCategoryName().trim().toLowerCase())
                .monthlyUpperLimit(createExpenseCategoryRequest.getMonthlyUpperLimit())
                .description(createExpenseCategoryRequest.getDescription())
                .build();

        ExpenseCategory saved = this.expenseCategoryRepository.save(expenseCategory);
        List<String> failedAddTags = this.tagService.addTags(saved.getId(), createExpenseCategoryRequest.getAddTags());
        return CreateExpenseCategoryResponse.builder()
                .id(saved.getId().toString())
                .failedAddTags(failedAddTags)
                .build();
    }


    private ExpenseCategoryElement mapToResponse(ExpenseCategory expenseCategory) {
        List<ExpenseTag> expenseTags = this.expenseTagRepository.findByCategoryId(expenseCategory.getId());
        List<ExpenseTagResponse> expenseTagsResponse = new ArrayList<>();
        for (ExpenseTag expenseTag : expenseTags) {
            expenseTagsResponse.add(
                    ExpenseTagResponse.builder()
                            .id(expenseTag.getId().toString())
                            .name(expenseTag.getName())
                            .build()
            );
        }

        return ExpenseCategoryElement.builder()
                .id(expenseCategory.getId().toString())
                .category(expenseCategory.getCategory())
                .monthlyUpperLimit(expenseCategory.getMonthlyUpperLimit())
                .description(expenseCategory.getDescription())
                .tags(expenseTagsResponse)
                .build();
    }


    public GetUserSettingsResponse getUserSettings(String userId) {

        List<ExpenseCategory> expenseCategories = this.expenseCategoryRepository.findByUserId(UUID.fromString(userId));
        return GetUserSettingsResponse.builder()
                .userId(userId)
                .expenseCategories(expenseCategories
                        .stream()
                        .map(this::mapToResponse)
                        .toList())
                .build();

    }


    public UpdateExpenseCategoryResponse updateExpenseCategory(
            String userId,
            String categoryId,
            UpdateExpenseCategoryRequest updatePersonalExpenseRequest) {

        // check if category to be updated exists or not
        Optional<ExpenseCategory> existingCategory = this.expenseCategoryRepository.findById(UUID.fromString(categoryId));
        if (!existingCategory.isPresent()) {
            throw new RuntimeException("Category to be updated does not exist");
        }

        String categoryNameInRequest = updatePersonalExpenseRequest.getCategoryName().trim().toLowerCase();
        boolean isCategoryNameInvalid = (!existingCategory.get().getCategory().equals(categoryNameInRequest)) &&
                this.expenseCategoryRepository.existsByUserIdAndCategory(UUID.fromString(userId), categoryNameInRequest);

        if (isCategoryNameInvalid) {
            throw new RuntimeException("Category name already in use");
        }

        List<String> failedAddTags = this.tagService.addTags(UUID.fromString(categoryId), updatePersonalExpenseRequest.getAddTags());
        List<String> failedUpateTags = this.tagService.updateTags(updatePersonalExpenseRequest.getUpdateTags());

        this.expenseCategoryRepository.updateById(
                UUID.fromString(categoryId),
                categoryNameInRequest,
                updatePersonalExpenseRequest.getDescription(),
                updatePersonalExpenseRequest.getMonthlyUpperLimit());

        return UpdateExpenseCategoryResponse.builder()
                .id(categoryId)
                .failedAddTags(failedAddTags)
                .failedUpdateTags(failedUpateTags)
                .build();

    }

}
