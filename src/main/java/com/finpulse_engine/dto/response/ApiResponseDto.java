package com.finpulse_engine.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseDto<T> {
    private String respId;
    private T data;
}
