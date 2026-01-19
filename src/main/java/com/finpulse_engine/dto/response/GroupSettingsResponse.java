package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupSettingsResponse {

    @NotNull
    private String groupId;

    @NotNull
    private List<ExpenseCategoryElement> expenseCategories;

}
