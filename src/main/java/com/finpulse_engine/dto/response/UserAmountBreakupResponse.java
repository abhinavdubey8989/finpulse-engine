package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAmountBreakupResponse {

    @NotNull
    private String userId;

    @NotNull
    private Integer expenseAmount;
}
