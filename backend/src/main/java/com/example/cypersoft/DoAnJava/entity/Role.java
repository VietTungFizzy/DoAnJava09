
package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;
}
    @Column(name = "name", unique = true, length = 50)
    private String name;
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}



