package com.example.storywatpad.model;

public class User {
    int UserId;
    String Username;
    String Email;
    String PasswordHash;
    String AvatarUrl;
    String Bio;
    String Role;
    String CreatedAt;
    String UpdatedAt;

    public User() {
    }

    public User(int userId, String username, String email, String passwordHash, String avatarUrl, String bio, String role, String createdAt, String updatedAt) {
        UserId = userId;
        Username = username;
        Email = email;
        PasswordHash = passwordHash;
        AvatarUrl = avatarUrl;
        Bio = bio;
        Role = role;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }
    public String getDrawableImageName() {
        return AvatarUrl.replace(".jpg", "").replace(".png", "").replace(".jpeg", "");
    }
}
