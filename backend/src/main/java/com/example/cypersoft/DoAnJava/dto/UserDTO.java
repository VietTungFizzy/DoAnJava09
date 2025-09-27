package com.example.cypersoft.DoAnJava.dto;

import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String roleName;
    private String password;
    private String phone;
    private String status;
    private String createdAt;


}
