package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.*;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.entity.*;
import com.finpulse_engine.enums.DifferentiatorType;
import com.finpulse_engine.enums.GroupRole;
import com.finpulse_engine.enums.SplitType;
import com.finpulse_engine.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private ExpenseTagRepository expenseTagRepository;

    @Autowired
    private GroupExpenseRepository groupExpenseRepository;

    @Autowired
    private TagService tagService;


    public List<String> addGroupMembers(UUID groupId, UUID createdByUserId, List<String> memeberIds) {
        List<String> failedMemberIds = new ArrayList<>();
        if (memeberIds == null || memeberIds.isEmpty()) {
            return failedMemberIds;
        }

        this.groupMembershipRepository.save(
                GroupMembership.builder()
                        .groupId(groupId)
                        .userId(createdByUserId)
                        .groupRoles(List.of(
                                GroupRole.G_ADMIN,
                                GroupRole.G_USER
                        ))
                        .build()
        );

        for (String memeberId : memeberIds) {
            if (!createdByUserId.toString().equals(memeberId) &&
                    !this.groupMembershipRepository.existsByGroupIdAndUserId(groupId, UUID.fromString(memeberId))) {
                this.groupMembershipRepository.save(
                        GroupMembership.builder()
                                .groupId(groupId)
                                .userId(UUID.fromString(memeberId))
                                .groupRoles(List.of(GroupRole.G_USER))
                                .build()
                );
            } else {
                failedMemberIds.add(memeberId);
            }
        }
        return failedMemberIds;
    }


    public CreateExpenseGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
        UUID createdByUserId = UUID.fromString(createGroupRequest.getCreatedBy());

        // check user-exists
        if (!this.userRepository.existsById(createdByUserId)) {
            throw new RuntimeException("User does not exist");
        }

        // check if group with same name should not exists
        if (this.expenseGroupRepository.existsByCreatedByAndName(createdByUserId, createGroupRequest.getName().toLowerCase())) {
            throw new RuntimeException("Group name already exist");
        }

        ExpenseGroup createdExpenseGroup = this.expenseGroupRepository.save(
                ExpenseGroup.builder()
                        .createdBy(createdByUserId)
                        .name(createGroupRequest.getName())
                        .description(createGroupRequest.getDescription())
                        .build()
        );

        List<String> failedMemberIds = this.addGroupMembers(
                createdExpenseGroup.getId(),
                createdByUserId,
                createGroupRequest.getMemberUserIds());


        return CreateExpenseGroupResponse.builder()
                .id(createdExpenseGroup.getId().toString())
                .failedMemberIds(failedMemberIds)
                .build();

    }


    public CreateExpenseCategoryResponse createGroupExpenseCategory(
            String groupId,
            CreateGroupExpenseCategoryRequest createGroupExpenseCategoryRequest) {
        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndCategory(
                UUID.fromString(groupId),
                createGroupExpenseCategoryRequest.getCategoryName().trim().toLowerCase()
        );

        if (categoryExists) {
            throw new RuntimeException("Category already exists");
        }

        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .userId(UUID.fromString(createGroupExpenseCategoryRequest.getUserId()))
                .type(DifferentiatorType.GROUP)
                .groupId(UUID.fromString(groupId))
                .category(createGroupExpenseCategoryRequest.getCategoryName().trim().toLowerCase())
                .monthlyUpperLimit(createGroupExpenseCategoryRequest.getMonthlyUpperLimit())
                .description(createGroupExpenseCategoryRequest.getDescription())
                .build();

        ExpenseCategory saved = this.expenseCategoryRepository.save(expenseCategory);
        List<String> failedAddTags = this.tagService.addTags(saved.getId(), createGroupExpenseCategoryRequest.getAddTags());
        return CreateExpenseCategoryResponse.builder()
                .id(saved.getId().toString())
                .failedAddTags(failedAddTags)
                .build();
    }


    public UpdateExpenseCategoryResponse updateExpenseCategory(
            String groupId,
            String categoryId,
            UpdateGroupExpenseCategoryRequest updateGroupExpenseCategoryRequest) {

        // check if category to be updated exists or not
        Optional<ExpenseCategory> existingCategory = this.expenseCategoryRepository.findById(UUID.fromString(categoryId));
        if (!existingCategory.isPresent()) {
            throw new RuntimeException("Category to be updated does not exist");
        }

        String categoryNameInRequest = updateGroupExpenseCategoryRequest.getCategoryName().trim().toLowerCase();
        boolean isCategoryNameInvalid = (!existingCategory.get().getCategory().equals(categoryNameInRequest)) &&
                this.expenseCategoryRepository.existsByGroupIdAndCategory(UUID.fromString(groupId), categoryNameInRequest);

        if (isCategoryNameInvalid) {
            throw new RuntimeException("Category name already in use");
        }

        List<String> failedAddTags = this.tagService.addTags(UUID.fromString(categoryId), updateGroupExpenseCategoryRequest.getAddTags());
        List<String> failedUpateTags = this.tagService.updateTags(updateGroupExpenseCategoryRequest.getUpdateTags());

        this.expenseCategoryRepository.updateById(
                UUID.fromString(categoryId),
                categoryNameInRequest,
                updateGroupExpenseCategoryRequest.getDescription(),
                updateGroupExpenseCategoryRequest.getMonthlyUpperLimit());

        return UpdateExpenseCategoryResponse.builder()
                .id(categoryId)
                .failedAddTags(failedAddTags)
                .failedUpdateTags(failedUpateTags)
                .build();

    }


    public void validateSplits(String groupId, CreateGroupExpenseRequest createGroupExpenseRequest) {

        SplitType splitType = createGroupExpenseRequest.getSplitType();
        Map<String, Integer> splits = createGroupExpenseRequest.getSplits();

        // check-1 : splits must have valid users ie. users must be present in system
        if (splits.isEmpty()) {
            throw new RuntimeException("Splits cannot be empty");
        }

        List<UUID> userIdsInvolvedRequest = new ArrayList<>(splits.keySet()).stream().map(UUID::fromString).toList();
        List<User> users = this.userRepository.findAllById(userIdsInvolvedRequest);
        if (users.isEmpty() || users.size() != splits.size()) {
            throw new RuntimeException("Invalid splits : user mis-match");
        }

        // check-2 : all users in splits must be part of the group
        List<GroupMembership> usersInGroup = this.groupMembershipRepository.findAllByGroupId(UUID.fromString(groupId));
        Set<UUID> userIdsInGroup = usersInGroup.stream().map(GroupMembership::getUserId).collect(Collectors.toSet());
        for (UUID userIdInRequest : userIdsInvolvedRequest) {
            if (!userIdsInGroup.contains(userIdInRequest)) {
                throw new RuntimeException("User id is not a part of the group : " + userIdInRequest);
            }
        }

        // check-3 : for %-split, the sum of values in splits map must add-up to 100
        if (splitType == SplitType.PERCENT) {
            int sumOfPercentages = 0;
            for (Integer percent : splits.values()) {
                if (percent == null || percent < 0 || percent > 100) {
                    throw new RuntimeException("Invalid percent  : null or not b/w 0-100");
                }
                sumOfPercentages += percent;
            }

            if (sumOfPercentages != 100) {
                throw new RuntimeException("Invalid sum of %-splits : " + sumOfPercentages);
            }
        }

        // check-4 : for exact-split, the sum of values in splits map must add-up to amount
        if (splitType == SplitType.EXACT) {
            int sumOfAmounts = 0;
            for (Integer amount : splits.values()) {
                if (amount == null || amount < 0) {
                    throw new RuntimeException("Invalid amount  : null or negative");
                }
                sumOfAmounts += amount;
            }
            if (sumOfAmounts != createGroupExpenseRequest.getAmount()) {
                throw new RuntimeException("Invalid sum of amounts : " + sumOfAmounts);
            }
        }
    }

    public Map<String, Integer> getDueAmounts(CreateGroupExpenseRequest request) {
        Map<String, Integer> dueAmounts = new HashMap<>();

        String paidByUserId = request.getPaidByUserId();
        int totalAmount = request.getAmount();
        SplitType splitType = request.getSplitType();
        Map<String, Integer> splits = request.getSplits();

        switch (splitType) {

            case EXACT -> {
                // splits: userId -> exact amount owed
                for (Map.Entry<String, Integer> entry : splits.entrySet()) {
                    String fromUserId = entry.getKey();
                    Integer amountOwed = entry.getValue();

                    if (fromUserId.equals(paidByUserId)) continue;

                    dueAmounts.put(fromUserId, amountOwed);
                }
            }

            case PERCENT -> {
                // splits: userId -> percentage of total amount
                int calculatedSum = 0;

                for (Map.Entry<String, Integer> entry : splits.entrySet()) {
                    String fromUserId = entry.getKey();
                    Integer percent = entry.getValue();

                    if (fromUserId.equals(paidByUserId)) continue;

                    int amountOwed = (int) Math.ceil((totalAmount * percent) / 100.0);
                    dueAmounts.put(fromUserId, amountOwed);
                    calculatedSum += amountOwed;
                }
            }

            default -> throw new RuntimeException("Unsupported split type: " + splitType);
        }

        return dueAmounts;
    }


    public CreateEntityResponse createGroupExpense(
            String groupId,
            CreateGroupExpenseRequest createGroupExpenseRequest) {

        String description = createGroupExpenseRequest.getDescription();
        String tagId = createGroupExpenseRequest.getTagId();

        if ((description == null && tagId == null) ||
                (description != null && description.isEmpty() && tagId != null && tagId.isEmpty())) {
            throw new RuntimeException("Either tags or description is needed");
        }

        boolean categoryExists = this.expenseCategoryRepository.existsByUserIdAndId(
                UUID.fromString(createGroupExpenseRequest.getPaidByUserId()),
                UUID.fromString(createGroupExpenseRequest.getCategoryId())
        );

        if (!categoryExists) {
            throw new RuntimeException("Category Not Found");
        }

        if (tagId != null && !this.expenseTagRepository.existsByIdAndCategoryId(
                UUID.fromString(tagId),
                UUID.fromString(createGroupExpenseRequest.getCategoryId()))) {
            throw new RuntimeException("Category & tag combination is invalid");
        }

        validateSplits(groupId, createGroupExpenseRequest);

        UUID dbTagId = StringUtils.hasText(createGroupExpenseRequest.getTagId())
                ? UUID.fromString(createGroupExpenseRequest.getTagId())
                : null;

        GroupExpense groupExpense = GroupExpense.builder()
                .paidBy(UUID.fromString(createGroupExpenseRequest.getPaidByUserId()))
                .categoryId(UUID.fromString(createGroupExpenseRequest.getCategoryId()))
                .groupId(UUID.fromString(groupId))
                .year(createGroupExpenseRequest.getYear())
                .month(createGroupExpenseRequest.getMonth())
                .tagId(dbTagId)
                .amount(createGroupExpenseRequest.getAmount())
                .description(createGroupExpenseRequest.getDescription())
                .splitType(createGroupExpenseRequest.getSplitType())
                .splits(createGroupExpenseRequest.getSplits())
                .dueAmounts(getDueAmounts(createGroupExpenseRequest))
                .build();

        GroupExpense saved = this.groupExpenseRepository.save(groupExpense);
        return new CreateEntityResponse(saved.getId().toString());
    }


}