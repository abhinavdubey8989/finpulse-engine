package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class GetPersonalExpenseResponse {

    @NotNull
    private Long id;

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

    @NotBlank
    private OffsetDateTime createdAt;

    @NotBlank
    private OffsetDateTime updatedAt;
}
