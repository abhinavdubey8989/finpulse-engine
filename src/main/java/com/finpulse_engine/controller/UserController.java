package com.finpulse_engine.controller;

import com.finpulse_engine.dto.request.CreateExpenseCategoryRequest;
import com.finpulse_engine.dto.request.UpdateExpenseCategoryRequest;
import com.finpulse_engine.dto.response.CreateExpenseCategoryResponse;
import com.finpulse_engine.dto.response.GetUserSettingsResponse;
import com.finpulse_engine.dto.response.UpdateExpenseCategoryResponse;
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
    public CreateExpenseCategoryResponse createPersonalExpenseCategory(
            @PathVariable String userId,
            @RequestBody CreateExpenseCategoryRequest createPersonalExpenseRequest) {
        return this.userService.createExpenseCategory(userId, createPersonalExpenseRequest);
    }

    @PutMapping("/{userId}/expense-category/{categoryId}")
    public UpdateExpenseCategoryResponse updatePersonalExpenseCategory(
            @PathVariable String userId,
            @PathVariable String categoryId,
            @RequestBody UpdateExpenseCategoryRequest updatePersonalExpenseRequest) {
        return this.userService.updateExpenseCategory(userId, categoryId, updatePersonalExpenseRequest);
    }

    @GetMapping("/{userId}/settings")
    public GetUserSettingsResponse getUserSettings(
            @PathVariable String userId) {
        return this.userService.getUserSettings(userId);
    }

}
