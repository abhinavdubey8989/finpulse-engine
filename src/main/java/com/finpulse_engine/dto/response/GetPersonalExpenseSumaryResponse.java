package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class GetPersonalExpenseSumaryResponse {

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
    private List<PersonalExpenseSumaryElement> elements;
}
