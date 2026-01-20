package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.LoginRequest;
import com.example.cypersoft.DoAnJava.dto.LoginResponse;
import com.example.cypersoft.DoAnJava.dto.RegisterRequest;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(@Lazy UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.authenticateUser(request.getEmail(), request.getPassword());
            User user = userService.getUserByEmail(request.getEmail());

            String roleName = user.getRole() != null ? user.getRole().getName() : null;
            return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), roleName, "Đăng nhập thành công"));
        } catch (Exception e) {
            // Avoid leaking internal messages
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, "Thông tin đăng nhập không hợp lệ"));
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
            // Avoid exposing raw exception
            System.out.println(e);
            return ResponseEntity.status(400).body("Đăng ký không thành công. Vui lòng kiểm tra thông tin.");
        }
    }
}
