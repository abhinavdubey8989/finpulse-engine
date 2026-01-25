package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupAndCategoryResponse {

    @NotNull
    private String groupId;

    @NotNull
    private String groupName;

    @NotNull
    private String groupDescription;

    @NotNull
    private List<ExpenseCategoryElement> expenseCategories;

    @NotNull
    private List<UserDetailResponse> members;
}
