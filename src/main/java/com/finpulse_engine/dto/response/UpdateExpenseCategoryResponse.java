package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateExpenseCategoryResponse {

    @NotNull
    private String id;

    @NotNull
    private List<String> failedAddTags;

    @NotNull
    private List<String> failedUpdateTags;
}
