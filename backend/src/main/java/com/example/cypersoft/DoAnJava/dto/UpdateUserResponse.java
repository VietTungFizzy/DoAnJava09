package com.example.cypersoft.DoAnJava.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
  private int id;
  private String name;
  private String phone;
  private String email;
  private int roleId;


}
