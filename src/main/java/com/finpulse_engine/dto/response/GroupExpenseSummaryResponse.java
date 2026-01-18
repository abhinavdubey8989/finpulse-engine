package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GroupExpenseSummaryResponse {

    @NotNull
    private Integer year;

    @NotBlank
    private Integer month;

    @NotNull
    private Integer numberOfExpenses;

    @NotBlank
    private Integer totalExpenseAmount;

    @NotBlank
    private Map<String, GroupExpenseSummaryUserDetail> users;

    @NotBlank
    private List<GroupExpenseSummaryElement> elements;

    @NotBlank
    private Map<String, Map<String, Integer>> dueAmounts;


}
