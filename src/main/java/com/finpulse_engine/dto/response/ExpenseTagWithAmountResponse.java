package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseTagWithAmountResponse {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private Integer expenseAmount;
}
