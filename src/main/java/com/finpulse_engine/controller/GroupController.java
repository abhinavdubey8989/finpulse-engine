package com.finpulse_engine.controller;

import com.finpulse_engine.dto.request.*;
import com.finpulse_engine.dto.response.*;
import com.finpulse_engine.service.GroupService;
import com.finpulse_engine.service.PersonalExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("")
    public CreateExpenseGroupResponse createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        return this.groupService.createGroup(createGroupRequest);
    }


    @PostMapping("/{groupId}/expense-category")
    public CreateExpenseCategoryResponse createGroupExpenseCategory(
            @PathVariable String groupId,
            @RequestBody CreateGroupExpenseCategoryRequest createGroupExpenseCategoryRequest) {
        return this.groupService.createGroupExpenseCategory(groupId, createGroupExpenseCategoryRequest);
    }


    @PutMapping("/{groupId}/expense-category/{categoryId}")
    public UpdateExpenseCategoryResponse updateGroupExpenseCategory(
            @PathVariable String groupId,
            @PathVariable String categoryId,
            @RequestBody UpdateGroupExpenseCategoryRequest updateGroupExpenseCategoryRequest) {
        return this.groupService.updateExpenseCategory(groupId, categoryId, updateGroupExpenseCategoryRequest);
    }


    @PostMapping("/{groupId}/expense")
    public Object addGroupExpense(
            @PathVariable String groupId,
            @RequestBody CreateGroupExpenseRequest createGroupExpenseRequest
    ) {
        return this.groupService.createGroupExpense(groupId, createGroupExpenseRequest);
    }


    @PutMapping("/{groupId}/expense/{expenseId}")
    public Object updateGroupExpense(@RequestBody Object loginRequest) {
        return null;
    }


    @PostMapping("/{groupId}/summary")
    public Object getGroupSummary(@RequestBody Object loginRequest) {
        return null;
    }

}
