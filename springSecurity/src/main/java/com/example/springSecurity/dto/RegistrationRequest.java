package com.example.springSecurity.dto;

public class RegistrationRequest {

    public RegistrationRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private String username;
    private String password;
    private String role;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
