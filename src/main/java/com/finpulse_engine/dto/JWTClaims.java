package com.finpulse_engine.dto;

import lombok.Data;

@Data
public class JWTClaims {
    String userId;
    String email;
    String roles;
}
