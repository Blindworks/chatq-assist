package com.chatq.assist.controller;

import com.chatq.assist.domain.entity.User;
import com.chatq.assist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/check-password")
    public Map<String, Object> checkPassword(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.put("found", false);
            response.put("message", "User not found");
            return response;
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());

        response.put("found", true);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("enabled", user.getEnabled());
        response.put("passwordMatches", matches);
        response.put("storedHash", user.getPassword());
        response.put("message", matches ? "Password matches!" : "Password does NOT match");

        return response;
    }

    @PostMapping("/reset-admin-password")
    public Map<String, String> resetAdminPassword() {
        Map<String, String> response = new HashMap<>();

        User admin = userRepository.findByUsername("admin").orElse(null);

        if (admin == null) {
            response.put("message", "Admin user not found");
            return response;
        }

        // Reset to admin123
        String newPassword = "admin123";
        String hashedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(hashedPassword);
        userRepository.save(admin);

        response.put("message", "Password reset successfully");
        response.put("username", "admin");
        response.put("newPassword", newPassword);
        response.put("newHash", hashedPassword);

        return response;
    }

    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("authenticated", false);
            response.put("message", "No authentication found");
            return response;
        }

        response.put("authenticated", true);
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        response.put("principal", authentication.getPrincipal().getClass().getName());

        return response;
    }
}
