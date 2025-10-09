package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.RegisterRequest;
import com.example.cypersoft.DoAnJava.dto.UpdateUserResponse;
import com.example.cypersoft.DoAnJava.dto.UserDTO;
import com.example.cypersoft.DoAnJava.entity.User;

import java.util.List;

public interface AdminService {
    List<UserDTO> getAllUsers();
    Boolean deleteUser(Integer id);
    Integer updateUser(UpdateUserResponse updateUser);
     User saveUser(RegisterRequest registerRequest);

}
