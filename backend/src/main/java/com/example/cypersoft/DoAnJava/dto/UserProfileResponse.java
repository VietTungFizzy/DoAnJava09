package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}
