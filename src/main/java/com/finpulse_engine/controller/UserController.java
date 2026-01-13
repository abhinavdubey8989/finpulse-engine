package com.finpulse_engine.controller;

import com.finpulse_engine.dto.request.CreateExpenseCategoryRequest;
import com.finpulse_engine.dto.response.CreateExpenseCategoryResponse;
import com.finpulse_engine.dto.response.GetUserSettingsResponse;
import com.finpulse_engine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/expense-category")
    public CreateExpenseCategoryResponse createPersonalExpense(
            @PathVariable String userId,
            @RequestBody CreateExpenseCategoryRequest createPersonalExpenseRequest) {
        return this.userService.createExpenseCategory(userId, createPersonalExpenseRequest);
    }

    @GetMapping("/{userId}/settings")
    public GetUserSettingsResponse getUserSettings(
            @PathVariable String userId) {
        return this.userService.getUserSettings(userId);
    }

}
