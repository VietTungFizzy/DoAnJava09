package com.example.cypersoft.DoAnJava.service.imp;

import com.example.cypersoft.DoAnJava.dto.UpdateUserResponse;
import com.example.cypersoft.DoAnJava.dto.UserDTO;
import com.example.cypersoft.DoAnJava.entity.Role;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.mapper.UserMapper;
import com.example.cypersoft.DoAnJava.repository.AdminRepository;
import com.example.cypersoft.DoAnJava.repository.RoleRepository;
import com.example.cypersoft.DoAnJava.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImp implements AdminService {
  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private RoleRepository roleRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        return adminRepository.findAll().stream().map(UserMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public Boolean deleteUser(Integer id) {
        Optional<User> userOpt = adminRepository.findById(id);
        if (userOpt.isPresent()){
            adminRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Integer UpdateUser(UpdateUserResponse updateUser) {
        try {
            // Kiểm tra user có tồn tại không
            Optional<User> userOpt = adminRepository.findById(updateUser.getId());
            if (userOpt.isEmpty()) {
                System.err.println("User with ID " + updateUser.getId() + " not found");
                return 0;
            }

            User user = userOpt.get();

            // Kiểm tra role có tồn tại không
            Optional<Role> roleOpt = roleRepository.findById(updateUser.getRoleId());
            if (roleOpt.isEmpty()) {
                System.err.println("Role with ID " + updateUser.getRoleId() + " not found");
                return 0;
            }

            // Update user fields
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setPhone(updateUser.getPhone());
            user.setRole(roleOpt.get()); // Sử dụng role đã tồn tại từ DB

            User savedUser = adminRepository.save(user);
            System.out.println("User updated successfully: " + savedUser.getId());
            return savedUser.getId();

        } catch (Exception e) {
            // Log error để debug
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

}


