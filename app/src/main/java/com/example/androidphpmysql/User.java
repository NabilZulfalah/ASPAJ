package com.example.androidphpmysql;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private String role;
    private String approvalStatus;

    public User(int id, String name, String email, String role, String approvalStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.approvalStatus = approvalStatus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }
}
