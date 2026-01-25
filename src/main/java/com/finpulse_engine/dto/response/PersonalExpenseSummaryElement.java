package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PersonalExpenseSummaryElement {

    @NotNull
    private String categoryId;

    @NotNull
    private String category;

    @NotNull
    private String categoryDescription;

    @NotNull
    private Long monthlyUpperLimit;

    @NotNull
    private Long monthlyExpenseDone;

    @NotBlank
    private List<ExpenseTagWithAmountResponse> tagBreakup;
}
