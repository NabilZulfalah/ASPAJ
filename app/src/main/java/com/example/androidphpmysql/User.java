package com.example.androidphpmysql;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private String role;
    private String approvalStatus;
    private String jurusan;

    public User(int id, String name, String email, String role, String approvalStatus, String jurusan) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.approvalStatus = approvalStatus;
        this.jurusan = jurusan;
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

    public String getJurusan() {
        return jurusan;
    }
}
