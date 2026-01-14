package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class GetPersonalExpenseResponse {

    @NotNull
    private String id;

    @NotNull
    private Integer year;

    @NotBlank
    private Integer month;

    @NotBlank
    private String categoryId;

    @NotBlank
    private String categoryName;

    @Positive
    private Integer amount;

    @NotBlank
    private String description;

    @NotBlank
    private OffsetDateTime createdAt;

    @NotBlank
    private OffsetDateTime updatedAt;

    private ExpenseTagResponse tag;
}
