package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.UpdateUserResponse;
import com.example.cypersoft.DoAnJava.dto.UserDTO;

import java.util.List;

public interface AdminService {
    List<UserDTO> getAllUsers();
    Boolean deleteUser(Integer id);
    Integer UpdateUser(UpdateUserResponse updateUser);

}
