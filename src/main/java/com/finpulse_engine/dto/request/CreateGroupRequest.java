package com.finpulse_engine.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {

    @NotBlank
    private String createdBy;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotEmpty
    private List<@NotBlank String> memberUserIds;
}
