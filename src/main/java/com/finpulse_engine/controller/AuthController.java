package com.finpulse_engine.controller;


import com.finpulse_engine.dto.LoginRequest;
import com.finpulse_engine.dto.LoginResponse;
import com.finpulse_engine.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest loginRequest) throws Exception {
        Object resp = this.authService.login(loginRequest);
        return resp;
    }

    @PostMapping("/login2")
    public ResponseEntity<LoginResponse> login2(@RequestBody LoginRequest loginRequest) throws Exception {
        ResponseEntity<LoginResponse> data =  ResponseEntity.ok(this.authService.login(loginRequest));
        return data;
    }
}
