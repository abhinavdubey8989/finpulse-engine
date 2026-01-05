package com.finpulse_engine.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String hashedPassword;
}
