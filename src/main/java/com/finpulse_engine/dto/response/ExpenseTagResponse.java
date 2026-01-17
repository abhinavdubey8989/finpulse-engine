package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpenseTagResponse {

    @NotNull
    private String id;

    @NotNull
    private String name;
}
