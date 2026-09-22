package com.supermarket.dto;

public class LoginResponse {
    private int userId;
    private String username;
    private String fullName;
    private String role;

    public LoginResponse(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
