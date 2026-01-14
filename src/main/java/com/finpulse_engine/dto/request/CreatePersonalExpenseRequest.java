package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CreatePersonalExpenseRequest {

    @NotNull
    private String userId;

    @NotNull
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotBlank
    private String categoryId;

    @Positive
    private Integer amount;

    private String description;

    private String tagId;
}
