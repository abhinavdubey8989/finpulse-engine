package com.finpulse_engine.dto.request;


import com.finpulse_engine.enums.SplitType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Map;

@Data
public class CreateGroupExpenseRequest {

    @NotNull
    private String paidByUserId;

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

    @NotBlank
    private SplitType splitType;

    @NotNull
    private Map<String, Integer> splits;
}
