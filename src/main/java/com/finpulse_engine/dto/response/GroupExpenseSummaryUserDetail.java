package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupExpenseSummaryUserDetail {

    @NotNull
    private String name;

    @NotNull
    private String emailId;

    @NotNull
    private int totalExpenseAmount;

    @NotNull
    private int expenseCount;
}
