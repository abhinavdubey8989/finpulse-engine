package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpenseSummaryRequest {

    @NotNull
    private String userId;

    @NotNull
    private Integer year;

    @NotBlank
    @Min(1)
    @Max(12)
    private Integer month;

}
