package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.LoginRequest;
import com.example.cypersoft.DoAnJava.dto.LoginResponse;
import com.example.cypersoft.DoAnJava.dto.RegisterRequest;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.authenticateUser(request.getEmail(), request.getPassword());
            User user = userService.getUserByEmail(request.getEmail());
            return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getRole(), "Đăng nhập thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setPhone(request.getPhone());

            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok("Đăng ký thành công với ID: " + savedUser.getId());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}


