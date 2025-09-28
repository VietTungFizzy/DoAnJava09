package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.UpdateUserResponse;
import com.example.cypersoft.DoAnJava.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

     @Autowired
     private AdminService adminService;

    @GetMapping("/listUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(adminService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách users: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            Boolean result = adminService.deleteUser(id);
            if (result) {
                return ResponseEntity.ok("Xóa user thành công");
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy user với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi xóa user: " + e.getMessage());
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserResponse updateUser) {
        try {
            Integer result = adminService.UpdateUser(updateUser);
            if (result > 0) {
                return ResponseEntity.ok("Cập nhật user thành công với ID: " + result);
            } else {
                return ResponseEntity.status(400).body("Không thể cập nhật user. Kiểm tra lại thông tin ID user và role ID.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật user: " + e.getMessage());
        }
    }

}


