package com.finpulse_engine.dto.response;

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
