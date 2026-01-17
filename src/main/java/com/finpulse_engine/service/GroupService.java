package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.CreateExpenseCategoryRequest;
import com.finpulse_engine.dto.request.CreateGroupRequest;
import com.finpulse_engine.dto.request.UpdateExpenseCategoryRequest;
import com.finpulse_engine.dto.request.UpdateExpenseTagRequest;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.entity.ExpenseCategory;
import com.finpulse_engine.entity.ExpenseGroup;
import com.finpulse_engine.entity.ExpenseTag;
import com.finpulse_engine.entity.GroupMembership;
import com.finpulse_engine.enums.GroupRole;
import com.finpulse_engine.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseGroupRepository expenseGroupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;


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

        GroupMembership finded = this.groupMembershipRepository.findByGroupIdAndUserId(
                createdExpenseGroup.getId(),
                createdByUserId
        );

        return CreateExpenseGroupResponse.builder()
                .id(createdExpenseGroup.getId().toString())
                .failedMemberIds(failedMemberIds)
                .build();

    }

}