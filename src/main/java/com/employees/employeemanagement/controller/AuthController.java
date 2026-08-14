package com.employees.employeemanagement.controller;

import com.employees.employeemanagement.entity.User;
import com.employees.employeemanagement.service.AuthService;
import com.employees.employeemanagement.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        return authService.login(
                request.getUsername(),
                request.getPassword()
        );
    }
}