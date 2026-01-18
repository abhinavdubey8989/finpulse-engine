package com.finpulse_engine.dto.request;


import com.finpulse_engine.enums.SplitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateGroupExpenseRequest {

    @NotBlank
    private String categoryId;

    @Positive
    private Integer amount;

    private String description;

    private String tagId;

    @NotBlank
    private SplitType splitType;

    @NotNull
    private Map<String, Integer> splits;
}
