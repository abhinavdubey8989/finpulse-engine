package com.finpulse_engine.service;

import com.finpulse_engine.dto.LoginRequest;
import com.finpulse_engine.dto.LoginResponse;
import com.finpulse_engine.entity.User;
import com.finpulse_engine.repository.UserRepository;
import com.finpulse_engine.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * Attempts login with email & password.
     *
     * @return Optional<User> if credentials are valid, else empty
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        if (!user.getPassword().equals(loginRequest.getHashedPassword())) {
            throw new RuntimeException("Invalid password");
        }

        LoginResponse response = LoginResponse.builder()
                .personalExpenseSettings(user.getExpenseSettings())
                .accessToken(this.jwtUtil.generateToken(
                         Map.of(
                        "userId", user.getId(),
                        "email", user.getEmail(),
                        "name", user.getName()
                )))
                .build();

        return response;
    }
}
