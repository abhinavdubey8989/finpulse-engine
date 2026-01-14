package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpenseCategoryElement {

    @NotNull
    private String id;

    @NotNull
    private String category;
    @NotNull
    private Integer monthlyUpperLimit;
    private String description;
    private List<ExpenseTagResponse> tags;
}
