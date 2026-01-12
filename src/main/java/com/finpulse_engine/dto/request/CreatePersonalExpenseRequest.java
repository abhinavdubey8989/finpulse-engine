package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePersonalExpenseRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Integer year;

    @NotBlank
    private String month;

    @NotBlank
    private String category;

    @Positive
    private Integer amount;

    @NotBlank
    private String description;
}
