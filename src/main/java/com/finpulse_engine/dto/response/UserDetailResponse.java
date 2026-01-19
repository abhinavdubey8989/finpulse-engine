package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDetailResponse {

    @NotNull
    private String userId;

    @NotNull
    private String name;

    @NotNull
    private String emailId;

}
