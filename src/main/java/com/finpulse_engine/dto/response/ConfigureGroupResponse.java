package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConfigureGroupResponse {

    @NotNull
    private List<UserDetailResponse> allUsers;

    @NotNull
    private List<GroupAndCategoryResponse> groupAndCategoryList;
}
