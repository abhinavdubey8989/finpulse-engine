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
import java.util.function.Function;
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


    public void validateSplits(
            String groupId,
            Integer expenseAmount,
            SplitType splitType,
            Map<String, Integer> splits) {

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
            if (sumOfAmounts != expenseAmount.intValue()) {
                throw new RuntimeException("Invalid sum of amounts : " + sumOfAmounts);
            }
        }
    }

    public Map<String, Integer> getDueAmounts(
            String paidByUserId,
            int expenseAmount,
            SplitType splitType,
            Map<String, Integer> splits) {
        Map<String, Integer> dueAmounts = new HashMap<>();
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
                for (Map.Entry<String, Integer> entry : splits.entrySet()) {
                    String fromUserId = entry.getKey();
                    Integer percent = entry.getValue();

                    if (fromUserId.equals(paidByUserId)) continue;

                    int amountOwed = (int) Math.ceil((expenseAmount * percent) / 100.0);
                    dueAmounts.put(fromUserId, amountOwed);
                }
            }

            default -> throw new RuntimeException("Unsupported split type: " + splitType);
        }

        return dueAmounts;
    }


    public EntityIdResponse createGroupExpense(
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

        validateSplits(
                groupId,
                createGroupExpenseRequest.getAmount(),
                createGroupExpenseRequest.getSplitType(),
                createGroupExpenseRequest.getSplits());

        UUID dbTagId = StringUtils.hasText(createGroupExpenseRequest.getTagId())
                ? UUID.fromString(createGroupExpenseRequest.getTagId())
                : null;

        Map<String, Integer> dueAmounts = this.getDueAmounts(
                createGroupExpenseRequest.getPaidByUserId(),
                createGroupExpenseRequest.getAmount(),
                createGroupExpenseRequest.getSplitType(),
                createGroupExpenseRequest.getSplits()
        );

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
                .dueAmounts(dueAmounts)
                .build();

        GroupExpense saved = this.groupExpenseRepository.save(groupExpense);
        return new EntityIdResponse(saved.getId().toString());
    }


    public EntityIdResponse updateGroupExpense(
            String groupId,
            String expenseId,
            UpdateGroupExpenseRequest updateGroupExpenseRequest) {

        // check-1 : either tag or description must be present
        String description = updateGroupExpenseRequest.getDescription();
        String tagId = updateGroupExpenseRequest.getTagId();
        if ((description == null && tagId == null) ||
                (description != null && description.isEmpty() && tagId != null && tagId.isEmpty())) {
            throw new RuntimeException("Either tags or description is needed");
        }

        // check-2 : the category , must belong to the group
        boolean categoryExists = this.expenseCategoryRepository.existsByIdAndGroupId(
                UUID.fromString(updateGroupExpenseRequest.getCategoryId()),
                UUID.fromString(groupId)
        );
        if (!categoryExists) {
            throw new RuntimeException("Category Not Found");
        }

        // check-3 : If tag is given, it must belong to the category
        if (tagId != null && !this.expenseTagRepository.existsByIdAndCategoryId(
                UUID.fromString(tagId),
                UUID.fromString(updateGroupExpenseRequest.getCategoryId()))) {
            throw new RuntimeException("Category & tag combination is invalid");
        }

        // check-4 : expense must exist
        Optional<GroupExpense> optionalGroupExpense = this.groupExpenseRepository.findById(UUID.fromString(expenseId));
        if (!optionalGroupExpense.isPresent()) {
            throw new RuntimeException("Group expense not found");
        }

        // check-4 : validate splits
        validateSplits(
                groupId,
                updateGroupExpenseRequest.getAmount(),
                updateGroupExpenseRequest.getSplitType(),
                updateGroupExpenseRequest.getSplits());

        UUID dbTagId = StringUtils.hasText(updateGroupExpenseRequest.getTagId())
                ? UUID.fromString(updateGroupExpenseRequest.getTagId())
                : null;

        Map<String, Integer> dueAmounts = this.getDueAmounts(
                optionalGroupExpense.get().getPaidBy().toString(),
                updateGroupExpenseRequest.getAmount(),
                updateGroupExpenseRequest.getSplitType(),
                updateGroupExpenseRequest.getSplits()
        );

        this.groupExpenseRepository.updateById(
                UUID.fromString(expenseId),
                UUID.fromString(updateGroupExpenseRequest.getCategoryId()),
                updateGroupExpenseRequest.getAmount(),
                updateGroupExpenseRequest.getDescription(),
                dbTagId,
                updateGroupExpenseRequest.getSplitType(),
                updateGroupExpenseRequest.getSplits(),
                dueAmounts
        );

        return new EntityIdResponse(expenseId);
    }

    public Map<String, GroupExpenseSummaryUserDetail> getGroupExpenseSummaryUserDetail(
            List<GroupMembership> groupMembershipList,
            List<GroupExpense> groupExpenseList
    ) {
        List<UUID> userIds = groupMembershipList.stream().map(GroupMembership::getUserId).toList();
        List<User> users = this.userRepository.findAllById(userIds);
        Map<String, GroupExpenseSummaryUserDetail> groupExpenseSummaryUserDetailMap = new HashMap<>();

        // 1 item refers to 1 user in the group
        for (GroupMembership groupMembership : groupMembershipList) {
            UUID userId = groupMembership.getUserId();
            User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found in GroupMembership with id: " + userId);
            }

            List<GroupExpense> expensesDoneByThisUser = groupExpenseList.stream()
                    .filter(e -> e.getPaidBy().equals(userId))
                    .toList();

            int totalExpenseDoneByThisUser = expensesDoneByThisUser.stream().mapToInt(GroupExpense::getAmount).sum();


            groupExpenseSummaryUserDetailMap.put(
                    userId.toString(),
                    GroupExpenseSummaryUserDetail.builder()
                            .name(user.getName())
                            .emailId(user.getEmail())
                            .expenseCount(expensesDoneByThisUser.size())
                            .totalExpenseAmount(totalExpenseDoneByThisUser)
                            .build()
            );


        }
        return groupExpenseSummaryUserDetailMap;
    }


    public Map<String, Map<String, Integer>> getGroupExpenseSummaryDueAmounts(List<GroupExpense> groupExpenseList) {
        Map<String, Map<String, Integer>> dueAmountResponse = new HashMap<>();

        for (GroupExpense groupExpense : groupExpenseList) {

            Map<String, Integer> currentExpenseDuesInDb = groupExpense.getDueAmounts();
            UUID receivingUserId = groupExpense.getPaidBy();

            Map<String, Integer> dueMapForReceivingUserId = dueAmountResponse.getOrDefault(
                    receivingUserId.toString(),
                    new HashMap<>());

            for (Map.Entry<String, Integer> entry : currentExpenseDuesInDb.entrySet()) {
                String sendingUserId = entry.getKey();
                int currentExpenseDueAmount = entry.getValue();
                int totalDueAmountTillNow = currentExpenseDueAmount + dueMapForReceivingUserId.getOrDefault(sendingUserId, 0);
                dueMapForReceivingUserId.put(sendingUserId, totalDueAmountTillNow);
            }

            dueAmountResponse.put(
                    receivingUserId.toString(),
                    dueMapForReceivingUserId
            );

        }

        return dueAmountResponse;
    }


    public GroupExpenseSummaryResponse getGroupExpenseSummary(
            String groupId,
            ExpenseSummaryRequest expenseSummaryRequest) {

        List<GroupExpense> groupExpenses = this.groupExpenseRepository.findByGroupIdAndYearAndMonth(
                UUID.fromString(groupId),
                expenseSummaryRequest.getYear(),
                expenseSummaryRequest.getMonth()
        );

        List<ExpenseCategory> categories = this.expenseCategoryRepository.findByTypeAndGroupId(
                DifferentiatorType.GROUP,
                UUID.fromString(groupId));

        List<UUID> categoryIds = categories.stream().map(ExpenseCategory::getId).collect(Collectors.toList());
        List<ExpenseTag> tags = this.expenseTagRepository.findByCategoryIdIn(categoryIds);
        List<GroupMembership> groupMembershipList = this.groupMembershipRepository.findAllByGroupId(UUID.fromString(groupId));


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
                groupExpenses.stream()
                        .collect(Collectors.groupingBy(
                                GroupExpense::getCategoryId,
                                Collectors.summingInt(GroupExpense::getAmount)
                        ));


        // Map<categoryId , Map<tagId, sum>>
        Map<UUID, Map<String, Integer>> categoryIdToTagIdToSumMap = new HashMap<>();
        for (GroupExpense groupExpense : groupExpenses) {
            String tagId = groupExpense.getTagId() == null ? "Others" : groupExpense.getTagId().toString();
            Map<String, Integer> tagIdToSumMap = categoryIdToTagIdToSumMap.getOrDefault(
                    groupExpense.getCategoryId(),
                    new HashMap<>()
            );
            tagIdToSumMap.put(
                    tagId,
                    groupExpense.getAmount() + tagIdToSumMap.getOrDefault(tagId, 0)
            );
            categoryIdToTagIdToSumMap.put(groupExpense.getCategoryId(), tagIdToSumMap);
        }

        // Map<categoryId , Map<userId, sum>>
        Map<UUID, Map<String, Integer>> categoryIdToUserIdToSumMap = new HashMap<>();
        for (GroupExpense groupExpense : groupExpenses) {
            String paidByUserId = groupExpense.getPaidBy().toString();
            Map<String, Integer> userIdToSumMap = categoryIdToUserIdToSumMap.getOrDefault(
                    groupExpense.getCategoryId(),
                    new HashMap<>()
            );
            userIdToSumMap.put(
                    paidByUserId,
                    groupExpense.getAmount() + userIdToSumMap.getOrDefault(paidByUserId, 0)
            );
            categoryIdToUserIdToSumMap.put(groupExpense.getCategoryId(), userIdToSumMap);
        }

        // Map <categoryId, List<tagBreakup>>
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

        // Map <categoryId, List<userBreakup>>
        Map<UUID, List<UserAmountBreakupResponse>> categoryIdToUserBreakup = new HashMap<>();
        for (GroupExpense groupExpense : groupExpenses) {

            List<UserAmountBreakupResponse> userBreakupForThisCategory = categoryIdToUserBreakup.getOrDefault(
                    groupExpense.getCategoryId(),
                    new ArrayList<>());

            String paidByUserId = groupExpense.getPaidBy().toString();

            UserAmountBreakupResponse userExpenseSoFarForCategory = userBreakupForThisCategory
                    .stream()
                    .filter(u -> u.getUserId().equals(paidByUserId))
                    .findFirst()
                    .orElse(UserAmountBreakupResponse.builder()
                            .expenseAmount(0)
                            .userId(paidByUserId)
                            .build());


            Integer expenseSoFar = userExpenseSoFarForCategory.getExpenseAmount();
            userExpenseSoFarForCategory.setExpenseAmount(groupExpense.getAmount() + expenseSoFar);

            userBreakupForThisCategory.removeIf(e -> e.getUserId().equals(paidByUserId));
            userBreakupForThisCategory.add(userExpenseSoFarForCategory);
            categoryIdToUserBreakup.put(
                    groupExpense.getCategoryId(),
                    userBreakupForThisCategory);
        }

        // Build summary elements
        List<GroupExpenseSummaryElement> elements = expenseSumByCategory.entrySet()
                .stream()
                .map(entry -> {

                    UUID categoryId = entry.getKey();
                    Integer totalSpent = entry.getValue();
                    ExpenseCategory category = categoryMap.get(categoryId);

                    return GroupExpenseSummaryElement.builder()
                            .categoryId(categoryId.toString())
                            .category(category.getCategory())
                            .categoryDescription(category.getDescription())
                            .monthlyUpperLimit(
                                    category.getMonthlyUpperLimit().longValue()
                            )
                            .monthlyExpenseDone(totalSpent.longValue())
                            .tagBreakup(categoryIdToExpenseTagWithAmount.getOrDefault(categoryId, new ArrayList<>()))
                            .userAmountBreakup(categoryIdToUserBreakup.getOrDefault(categoryId, new ArrayList<>()))
                            .build();
                })
                .toList();

        int totalExpenseAmount = groupExpenses.stream().mapToInt(GroupExpense::getAmount).sum();

        return GroupExpenseSummaryResponse.builder()
                .year(expenseSummaryRequest.getYear())
                .month(expenseSummaryRequest.getMonth())
                .numberOfExpenses(groupExpenses.size())
                .totalExpenseAmount(totalExpenseAmount)
                .users(getGroupExpenseSummaryUserDetail(groupMembershipList, groupExpenses))
                .elements(elements)
                .dueAmounts(getGroupExpenseSummaryDueAmounts(groupExpenses))
                .build();
    }


}