package com.finpulse_engine.dto.response;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class LoginResponse {
    private String accessToken;
    private String userId;
}
