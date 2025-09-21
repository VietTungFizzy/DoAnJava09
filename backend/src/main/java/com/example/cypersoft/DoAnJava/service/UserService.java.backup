package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }

    // registerUser implemented below with email sending and lock fields initialization

    public String authenticateUser(String email, String password) {
        User user = getUserByEmail(email);

        // Check permanent lock
        if (Boolean.TRUE.equals(user.getPermanentlyLocked())) {
            throw new RuntimeException("Tài khoản đang tạm khóa, liên hệ admin");
        }

        // Check temporary lock
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Tài khoản đang bị khóa tạm thời đến: " + user.getLockedUntil());
        }

        // Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0) + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) {
                // Lock for 15 minutes
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                user.setFailedLoginAttempts(0);
            }

            userRepository.save(user);

            // If locked temporary just now
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Đăng nhập sai quá 5 lần. Tài khoản bị khóa 15 phút");
            }

            throw new RuntimeException("Sai mật khẩu");
        }

        // Password correct
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            // Still within lock time even if password is correct
            throw new RuntimeException("Tài khoản đang bị khóa tạm thời đến: " + user.getLockedUntil());
        }

        // Reset attempts and lock
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        return jwtUtil.generateToken(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setStatus("active");
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setPermanentlyLocked(false);

        User saved = userRepository.save(user);

        // Send welcome email
        try {
            emailService.sendSimpleMail(
                saved.getEmail(),
                "Chào mừng bạn đến với hệ thống",
                "Xin chào " + (saved.getName() == null ? "bạn" : saved.getName()) + ",\n\nBạn đã đăng ký thành công tài khoản.\n\nTrân trọng."
            );
        } catch (Exception ignored) {}

        return saved;
    }
}