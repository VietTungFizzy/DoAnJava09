package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.ChangePasswordRequest;
import com.example.cypersoft.DoAnJava.dto.UpdateProfileRequest;
import com.example.cypersoft.DoAnJava.dto.UserProfileResponse;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return getUserByEmail(userDetails.getUsername());
        }
        throw new RuntimeException("User not authenticated");
    }

    public UserProfileResponse getUserProfile() {
        User user = getCurrentUser();
        return new UserProfileResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }

    public String updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user.setPhone(request.getPhone().trim());
        }
        
        userRepository.save(user);
        return "Cập nhật thông tin thành công";
    }

    public String changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        
        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        
        // Check if new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Send notification email
        try {
            emailService.sendSimpleMail(
                user.getEmail(),
                "Thay đổi mật khẩu thành công",
                "Xin chào " + (user.getName() == null ? "bạn" : user.getName()) + ",\n\n" +
                "Mật khẩu của bạn đã được thay đổi thành công.\n" +
                "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n" +
                "Trân trọng."
            );
        } catch (Exception ignored) {}
        
        return "Thay đổi mật khẩu thành công";
    }

    public String requestAccountDeletion(String password, String reason) {
        User user = getCurrentUser();
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }
        
        // Set status to pending deletion
        user.setStatus("pending_deletion");
        userRepository.save(user);
        
        // Send notification email to admin
        try {
            emailService.sendSimpleMail(
                "admin@example.com", // Replace with actual admin email
                "Yêu cầu xóa tài khoản - " + user.getEmail(),
                "Người dùng " + user.getName() + " (" + user.getEmail() + ") đã yêu cầu xóa tài khoản.\n\n" +
                "Lý do: " + (reason != null ? reason : "Không có lý do") + "\n\n" +
                "Vui lòng xem xét và xử lý yêu cầu này."
            );
        } catch (Exception ignored) {}
        
        // Send confirmation email to user
        try {
            emailService.sendSimpleMail(
                user.getEmail(),
                "Yêu cầu xóa tài khoản đã được gửi",
                "Xin chào " + (user.getName() == null ? "bạn" : user.getName()) + ",\n\n" +
                "Chúng tôi đã nhận được yêu cầu xóa tài khoản của bạn.\n" +
                "Yêu cầu này sẽ được xem xét và xử lý trong vòng 24-48 giờ.\n" +
                "Trong thời gian chờ đợi, tài khoản của bạn sẽ bị tạm khóa.\n\n" +
                "Trân trọng."
            );
        } catch (Exception ignored) {}
        
        return "Yêu cầu xóa tài khoản đã được gửi. Chúng tôi sẽ xử lý trong vòng 24-48 giờ.";
    }
}