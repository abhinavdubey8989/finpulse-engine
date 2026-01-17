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


    @PutMapping("/{groupId}")
    public Object updateGroup(@RequestBody Object loginRequest) {
        return null;
    }


    @PostMapping("/{groupId}/expense")
    public Object addGroupExpense(@RequestBody Object loginRequest) {
        return null;
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
