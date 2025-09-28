package com.example.cypersoft.DoAnJava.mapper;

import com.example.cypersoft.DoAnJava.dto.UserDTO;
import com.example.cypersoft.DoAnJava.entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        
        // Kiểm tra null để tránh NullPointerException
        if (user.getRole() != null) {
            userDTO.setRoleId(user.getRole().getId());
        } else {
            userDTO.setRoleId(0); // hoặc giá trị default khác
        }
        
        userDTO.setPassword(user.getPassword());
        userDTO.setPhone(user.getPhone());
        userDTO.setStatus(user.getStatus());
        userDTO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return userDTO;
    }
}
