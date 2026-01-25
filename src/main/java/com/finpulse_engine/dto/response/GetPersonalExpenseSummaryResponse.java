package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetPersonalExpenseSummaryResponse {

    @NotNull
    private String userId;

    @NotNull
    private Integer year;

    @NotBlank
    private Integer month;

    @NotNull
    private Integer numberOfExpenses;

    @NotBlank
    private Integer totalExpenseAmount;

    @NotBlank
    private List<PersonalExpenseSummaryElement> elements;
}
