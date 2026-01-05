package com.finpulse_engine.dto;

import com.finpulse_engine.entity.UserExpenseSetting;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
@Data
public class LoginResponse {
    private String accessToken;
    private List<UserExpenseSetting> personalExpenseSettings;
}
