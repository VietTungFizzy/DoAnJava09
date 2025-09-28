package com.example.cypersoft.DoAnJava.dto;

import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private int roleId;
    private String password;
    private String phone;
    private String status;
    private String createdAt;


}
