package com.supermarket.controller;

import com.supermarket.dao.UserDAO;
import com.supermarket.dto.LoginRequest;
import com.supermarket.dto.LoginResponse;
import com.supermarket.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userDAO.authenticate(request.getUsername(), request.getPassword());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
            }
            return ResponseEntity.ok(new LoginResponse(
                user.getUserId(), user.getUsername(), user.getFullName(), user.getRole()
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") int id) {
        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }
            return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole()
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") int id, @RequestBody Map<String, String> payload) {
        String fullName = payload.get("fullName");
        String username = payload.get("username");
        String newPassword = payload.get("password");

        if (fullName == null || fullName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Full name cannot be empty"));
        }
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username cannot be empty"));
        }

        try {
            User existing = userDAO.getUserById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            // If username is changing, ensure it's not taken by someone else
            if (!existing.getUsername().equalsIgnoreCase(username.trim())) {
                User check = userDAO.getUserByUsername(username.trim());
                if (check != null && check.getUserId() != id) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
                }
            }

            boolean updated = userDAO.updateUser(id, fullName, username, newPassword);
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "userId", id,
                    "username", username.trim(),
                    "fullName", fullName.trim(),
                    "role", existing.getRole(),
                    "message", "Profile updated successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update profile"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/cashiers")
    public ResponseEntity<?> getCashiers() {
        try {
            java.util.List<User> list = userDAO.getCashiers();
            java.util.List<Map<String, Object>> result = list.stream().map(u -> {
                Map<String, Object> m = new java.util.HashMap<>();
                m.put("userId", u.getUserId());
                m.put("username", u.getUsername());
                m.put("fullName", u.getFullName());
                m.put("role", u.getRole());
                return m;
            }).toList();
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/cashiers")
    public ResponseEntity<?> addCashier(@RequestBody Map<String, String> payload) {
        String fullName = payload.get("fullName");
        String username = payload.get("username");
        String password = payload.get("password");

        if (fullName == null || fullName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cashier full name is required"));
        }
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
        }

        try {
            User existing = userDAO.getUserByUsername(username.trim());
            if (existing != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken by another user"));
            }

            User cashier = new User(0, username.trim(), password, fullName.trim(), "CASHIER");
            if (userDAO.addCashier(cashier)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "userId", cashier.getUserId(),
                    "username", cashier.getUsername(),
                    "fullName", cashier.getFullName(),
                    "role", cashier.getRole(),
                    "message", "Cashier account created successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to add cashier"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/cashiers/{id}")
    public ResponseEntity<?> updateCashier(@PathVariable("id") int id, @RequestBody Map<String, String> payload) {
        String fullName = payload.get("fullName");
        String username = payload.get("username");
        String password = payload.get("password");

        if (fullName == null || fullName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cashier full name is required"));
        }
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }

        try {
            User existing = userDAO.getUserById(id);
            if (existing == null || !"CASHIER".equalsIgnoreCase(existing.getRole())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cashier not found"));
            }

            if (!existing.getUsername().equalsIgnoreCase(username.trim())) {
                User check = userDAO.getUserByUsername(username.trim());
                if (check != null && check.getUserId() != id) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
                }
            }

            boolean updated = userDAO.updateUser(id, fullName, username, password);
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "userId", id,
                    "username", username.trim(),
                    "fullName", fullName.trim(),
                    "role", "CASHIER",
                    "message", "Cashier updated successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to update cashier"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/cashiers/{id}")
    public ResponseEntity<?> deleteCashier(@PathVariable("id") int id) {
        try {
            boolean deleted = userDAO.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Cashier account deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not delete cashier. Admin accounts cannot be deleted."));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
