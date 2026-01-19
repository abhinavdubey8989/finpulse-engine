package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.request.ExpenseSummaryRequest;
import com.finpulse_engine.dto.request.UpdatePersonalExpenseRequest;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.entity.ExpenseTag;
import com.finpulse_engine.entity.PersonalExpense;
import com.finpulse_engine.enums.DifferentiatorType;
import com.finpulse_engine.repository.ExpenseTagRepository;
import com.finpulse_engine.repository.PersonalExpenseRepository;
import com.finpulse_engine.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private ExpenseTagRepository expenseTagRepository;


    public EntityIdResponse createExpense(CreatePersonalExpenseRequest createPersonalExpenseRequest) {

        String description = createPersonalExpenseRequest.getDescription();
        String tagId = createPersonalExpenseRequest.getTagId();

        if ((description == null && tagId == null) ||
                (description != null && description.isEmpty() && tagId != null && tagId.isEmpty())) {
            throw new RuntimeException("Either tags or description is needed");
        }

        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndId(
                UUID.fromString(createPersonalExpenseRequest.getUserId()),
                UUID.fromString(createPersonalExpenseRequest.getCategoryId())
        );

        if (!categoryExists) {
            throw new RuntimeException("Category Not Found");
        }

        if (tagId != null && !this.expenseTagRepository.existsByIdAndCategoryId(
                UUID.fromString(tagId),
                UUID.fromString(createPersonalExpenseRequest.getCategoryId()))) {
            throw new RuntimeException("Category & tag combination is invalid");
        }

        UUID dbTagId = StringUtils.hasText(createPersonalExpenseRequest.getTagId())
                ? UUID.fromString(createPersonalExpenseRequest.getTagId())
                : null;

        PersonalExpense expense = PersonalExpense.builder()
                .userId(UUID.fromString(createPersonalExpenseRequest.getUserId()))
                .year(createPersonalExpenseRequest.getYear())
                .month(createPersonalExpenseRequest.getMonth())
                .categoryId(UUID.fromString(createPersonalExpenseRequest.getCategoryId()))
                .tagId(dbTagId)
                .amount(createPersonalExpenseRequest.getAmount())
                .description(createPersonalExpenseRequest.getDescription())
                .build();


        PersonalExpense saved = this.personalExpenseRepository.save(expense);
        return new EntityIdResponse(saved.getId().toString());
    }

    private GetPersonalExpenseResponse mapToResponse(PersonalExpense expense) {
        UUID tagId = expense.getTagId();
        ExpenseTagResponse expenseTagResponse = null;
        if (tagId != null) {
            ExpenseTag tag = this.expenseTagRepository.findById(tagId).get();
            expenseTagResponse = ExpenseTagResponse.builder()
                    .name(tag.getName())
                    .id(tag.getId().toString())
                    .build();
        }

        ExpenseCategory expenseCategory = this.expenseCategoryRepository.findById(expense.getCategoryId()).get();

        return GetPersonalExpenseResponse.builder()
                .id(expense.getId().toString())
                .year(expense.getYear())
                .month(expense.getMonth())
                .categoryId(expense.getCategoryId().toString())
                .categoryName(expenseCategory.getCategory())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .tag(expenseTagResponse)
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


    public GetPersonalExpenseSummaryResponse getPersonalExpenseSummary(String userId, ExpenseSummaryRequest expenseSummaryRequest) {
        List<PersonalExpense> expenses = this.personalExpenseRepository.findByUserIdAndYearAndMonth(
                UUID.fromString(userId),
                expenseSummaryRequest.getYear(),
                expenseSummaryRequest.getMonth()
        );

        List<ExpenseCategory> categories = this.expenseCategoryRepository.findByUserIdAndType(
                UUID.fromString(userId),
                DifferentiatorType.PERSONAL);

        List<UUID> categoryIds = categories.stream().map(ExpenseCategory::getId).collect(Collectors.toList());
        List<ExpenseTag> tags = this.expenseTagRepository.findByCategoryIdIn(categoryIds);

        // UUID -> category map (for fast lookup)
        Map<UUID, ExpenseCategory> categoryMap =
                categories.stream()
                        .collect(Collectors.toMap(
                                ExpenseCategory::getId,
                                Function.identity()
                        ));

        // UUID -> tag map (for fast lookup)
        Map<UUID, ExpenseTag> tagMap =
                tags.stream()
                        .collect(Collectors.toMap(
                                ExpenseTag::getId,
                                Function.identity()
                        ));


        // UUID -> List<UUID> (category -> tagIds)
        Map<UUID, List<UUID>> categoryToTagsMap = new HashMap<>();
        for (ExpenseCategory category : categories) {
            List<UUID> tagsOfCategory = new ArrayList<>();
            for (ExpenseTag tag : tags) {
                if (category.getId().equals(tag.getCategoryId())) {
                    tagsOfCategory.add(tag.getId());
                }
            }
            categoryToTagsMap.put(category.getId(), tagsOfCategory);
        }

        // UUID -> UUID (tagId -> category)
        Map<UUID, UUID> tagIdToCategoryIdMap =
                tags.stream().collect(Collectors.toMap(
                        ExpenseTag::getId,
                        ExpenseTag::getCategoryId
                ));


        // Group expenses by categoryId and sum amounts
        Map<UUID, Integer> expenseSumByCategory =
                expenses.stream()
                        .collect(Collectors.groupingBy(
                                PersonalExpense::getCategoryId,
                                Collectors.summingInt(PersonalExpense::getAmount)
                        ));


        Map<UUID, Map<String, Integer>> categoryIdToTagIdToSumMap = new HashMap<>();
        for (PersonalExpense expense : expenses) {
            String tagId = expense.getTagId() == null ? "Others" : expense.getTagId().toString();
            Map<String, Integer> tagIdToSumMap = categoryIdToTagIdToSumMap.getOrDefault(
                    expense.getCategoryId(),
                    new HashMap<>()
            );
            tagIdToSumMap.put(
                    tagId,
                    expense.getAmount() + tagIdToSumMap.getOrDefault(tagId, 0)
            );
            categoryIdToTagIdToSumMap.put(expense.getCategoryId(), tagIdToSumMap);
        }

        Map<UUID, List<ExpenseTagWithAmountResponse>> categoryIdToExpenseTagWithAmount = new HashMap<>();
        for (Map.Entry<UUID, List<UUID>> categoryToTagsMapEntry : categoryToTagsMap.entrySet()) {
            UUID categoryId = categoryToTagsMapEntry.getKey();
            List<ExpenseTagWithAmountResponse> expenseTagsWithAmountForCategory = categoryIdToExpenseTagWithAmount.getOrDefault(categoryId, new ArrayList<>());
            for (UUID tagId : categoryToTagsMapEntry.getValue()) {
                int expenseAmount = categoryIdToTagIdToSumMap.getOrDefault(categoryId, new HashMap<>()).getOrDefault(tagId.toString(), 0);
                expenseTagsWithAmountForCategory.add(
                        ExpenseTagWithAmountResponse.builder()
                                .id(tagId.toString())
                                .name(tagMap.get(tagId).getName())
                                .expenseAmount(expenseAmount)
                                .build()
                );
            }

            if (categoryIdToTagIdToSumMap.containsKey(categoryId)) {
                expenseTagsWithAmountForCategory.add(
                        ExpenseTagWithAmountResponse.builder()
                                .id("Others")
                                .name("Others")
                                .expenseAmount(categoryIdToTagIdToSumMap.get(categoryId).getOrDefault("Others", 0))
                                .build()
                );
            }

            categoryIdToExpenseTagWithAmount.put(
                    categoryToTagsMapEntry.getKey(),
                    expenseTagsWithAmountForCategory);
        }

        // Build summary elements
        List<PersonalExpenseSummaryElement> elements = expenseSumByCategory.entrySet()
                .stream()
                .map(entry -> {

                    UUID categoryId = entry.getKey();
                    Integer totalSpent = entry.getValue();
                    ExpenseCategory category = categoryMap.get(categoryId);

                    return PersonalExpenseSummaryElement.builder()
                            .categoryId(categoryId.toString())
                            .category(category.getCategory())
                            .categoryDescription(category.getDescription())
                            .monthlyUpperLimit(
                                    category.getMonthlyUpperLimit().longValue()
                            )
                            .monthlyExpenseDone(totalSpent.longValue())
                            .tagBreakup(categoryIdToExpenseTagWithAmount.getOrDefault(categoryId, new ArrayList<>()))
                            .build();
                })
                .toList();

        int totalExpenseAmount = expenses.stream().mapToInt(expense -> expense.getAmount()).sum();

        return GetPersonalExpenseSummaryResponse.builder()
                .userId(userId)
                .year(expenseSummaryRequest.getYear())
                .month(expenseSummaryRequest.getMonth())
                .numberOfExpenses(expenses.size())
                .totalExpenseAmount(totalExpenseAmount)
                .elements(elements)
                .build();
    }

    public EntityIdResponse updateExpense(
            String expenseId,
            UpdatePersonalExpenseRequest updatePersonalExpenseRequest) {

        String description = updatePersonalExpenseRequest.getDescription();
        String tagId = updatePersonalExpenseRequest.getTagId();

        if ((description == null && tagId == null) ||
                (description != null && description.isEmpty() && tagId != null && tagId.isEmpty())) {
            throw new RuntimeException("Either tags or description is needed");
        }

        if (tagId != null && !this.expenseTagRepository.existsByIdAndCategoryId(
                UUID.fromString(tagId),
                UUID.fromString(updatePersonalExpenseRequest.getCategoryId()))) {
            throw new RuntimeException("Category & tag combination is invalid");
        }

        // check if expense exists
        Optional<PersonalExpense> optionalPersonalExpense = this.personalExpenseRepository.findById(UUID.fromString(expenseId));
        if (!optionalPersonalExpense.isPresent()) {
            throw new RuntimeException("Expense not found");
        }

        UUID dbTagId = StringUtils.hasText(updatePersonalExpenseRequest.getTagId())
                ? UUID.fromString(updatePersonalExpenseRequest.getTagId())
                : null;

        this.personalExpenseRepository.updateById(
                UUID.fromString(expenseId),
                UUID.fromString(updatePersonalExpenseRequest.getCategoryId()),
                updatePersonalExpenseRequest.getAmount(),
                updatePersonalExpenseRequest.getDescription(),
                dbTagId
        );

        return new EntityIdResponse(expenseId);
    }

}
