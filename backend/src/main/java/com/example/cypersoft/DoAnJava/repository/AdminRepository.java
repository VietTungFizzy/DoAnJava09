package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.dto.UserDTO;
import com.example.cypersoft.DoAnJava.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository  extends JpaRepository<User,Integer> {

}
