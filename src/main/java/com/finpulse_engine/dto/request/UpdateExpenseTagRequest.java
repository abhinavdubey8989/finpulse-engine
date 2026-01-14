package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UpdateExpenseTagRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String newName;
}
