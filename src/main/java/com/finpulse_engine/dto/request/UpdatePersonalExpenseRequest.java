package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdatePersonalExpenseRequest {

    @NotBlank
    private String categoryId;

    @Positive
    private Integer amount;

    private String description;

    private String tagId;
}
