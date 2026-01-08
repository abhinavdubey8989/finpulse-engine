package com.finpulse_engine.controller;

import com.finpulse_engine.dto.request.CreatePersonalExpenseRequest;
import com.finpulse_engine.dto.response.CreatePersonalExpenseResponse;
import com.finpulse_engine.dto.response.GetPersonalExpenseResponse;
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
    public CreatePersonalExpenseResponse createPersonalExpense(@RequestBody CreatePersonalExpenseRequest createPersonalExpenseRequest) {
        return this.personalExpenseService.createExpense(createPersonalExpenseRequest);
    }

    @GetMapping("/{userId}")
    public List<GetPersonalExpenseResponse> getAllPersonalExpenses(@PathVariable Long userId) {
        return this.personalExpenseService.getAllPersonalExpenses(userId);
    }
}
