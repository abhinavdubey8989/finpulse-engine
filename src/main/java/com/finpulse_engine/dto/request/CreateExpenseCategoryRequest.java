package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateExpenseCategoryRequest {

    @NotBlank
    private String category;

    @NotNull
    @Positive
    private Integer monthlyUpperLimit;

    @NotBlank
    private String description;
}
