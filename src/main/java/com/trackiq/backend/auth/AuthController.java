package com.trackiq.backend.auth;

import com.trackiq.backend.dto.LoginRequest;
import com.trackiq.backend.entity.User;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // frontend URL
public class AuthController {

    private final AuthService authService;

    // ✅ Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 🔹 Signup
    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return authService.signup(user);
    }

    // 🔹 Login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}