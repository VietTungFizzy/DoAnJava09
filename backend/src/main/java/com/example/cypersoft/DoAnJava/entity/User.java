package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "status")
    private String status = "active";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Login lockout controls
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil; // tạm khóa đến thời điểm này

    @Column(name = "permanently_locked")
    private Boolean permanentlyLocked = false; // khóa vĩnh viễn, liên hệ admin

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserAddress> addresses;

    public java.util.Optional<UserAddress> getDefaultShippingAddress() {
        if (addresses == null) return java.util.Optional.empty();
        return addresses.stream()
                .filter(a -> "shipping".equalsIgnoreCase(a.getType()) && Boolean.TRUE.equals(a.getIsDefault()))
                .findFirst();
    }

    public java.util.Optional<UserAddress> getAnyShippingAddress() {
        if (addresses == null) return java.util.Optional.empty();
        return addresses.stream()
                .filter(a -> "shipping".equalsIgnoreCase(a.getType()))
                .findFirst();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String roleName = (role == null || role.getName() == null) ? "buyer" : role.getName();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "active".equals(status) && Boolean.FALSE.equals(permanentlyLocked)
                && (lockedUntil == null || lockedUntil.isBefore(LocalDateTime.now()));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "active".equals(status) && Boolean.FALSE.equals(permanentlyLocked);
    }
}