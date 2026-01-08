package com.finpulse_engine.controller;


import com.finpulse_engine.dto.request.LoginRequest;
import com.finpulse_engine.dto.response.LoginResponse;
import com.finpulse_engine.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login2(@RequestBody LoginRequest loginRequest) throws Exception {
        return this.authService.login(loginRequest);
    }
}
