package com.finpulse_engine.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateExpenseCategoryResponse {

    @NotNull
    private String id;
}
