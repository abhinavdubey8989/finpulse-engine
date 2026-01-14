package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class UpdateExpenseCategoryRequest {

    @NotBlank
    private String categoryName;

    @NotNull
    @Positive
    private Integer monthlyUpperLimit;

    @NotBlank
    private String description;

    private List<String> addTags;

    private List<UpdateExpenseTagRequest> updateTags;
}
