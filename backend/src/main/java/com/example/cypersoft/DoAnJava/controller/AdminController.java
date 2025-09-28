package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.service.AdminService;
import com.example.cypersoft.DoAnJava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

     @Autowired
     private AdminService adminService;

    @GetMapping("/listUsers")
    public ResponseEntity<?> getAllUsers() {
          return ResponseEntity.ok(adminService.getAllUsers());
    }
}
