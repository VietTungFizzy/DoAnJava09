package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "depth")
    private Integer depth = 0;

    @ManyToMany(mappedBy = "categories")
    private List<Product> products;
}

