package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSettingsResponse {

    @NotNull
    private String userId;

    @NotNull
    private List<ExpenseCategoryElement> expenseCategories;

}
