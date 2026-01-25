package com.finpulse_engine.controller;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.request.ExpenseSummaryRequest;
import com.finpulse_engine.dto.request.UpdatePersonalExpenseRequest;
import com.finpulse_engine.dto.response.EntityIdResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseSummaryResponse;
import com.finpulse_engine.service.PersonalExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/expense/personal")
@RequiredArgsConstructor
public class PersonalExpenseController {

    @Autowired
    private PersonalExpenseService personalExpenseService;

    @PostMapping("")
    public EntityIdResponse createPersonalExpense(@RequestBody CreatePersonalExpenseRequest createPersonalExpenseRequest) {
        return this.personalExpenseService.createExpense(createPersonalExpenseRequest);
    }

    @PutMapping("/{expenseId}")
    public EntityIdResponse createPersonalExpense(
            @PathVariable String expenseId,
            @RequestBody UpdatePersonalExpenseRequest updatePersonalExpenseRequest) {
        return this.personalExpenseService.updateExpense(
                expenseId,
                updatePersonalExpenseRequest);
    }


    @GetMapping("/{userId}")
    public List<GetPersonalExpenseResponse> getAllPersonalExpenses(@PathVariable String userId) {
        return this.personalExpenseService.getAllPersonalExpenses(userId);
    }

    @PostMapping("/{userId}/summary")
    public GetPersonalExpenseSummaryResponse getPersonalExpenseSummary(
            @PathVariable String userId,
            @RequestBody ExpenseSummaryRequest expenseSummaryRequest
            ) {
        return this.personalExpenseService.getPersonalExpenseSummary(userId, expenseSummaryRequest);
    }
}
