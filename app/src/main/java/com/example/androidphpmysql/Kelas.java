package com.example.androidphpmysql;

import java.io.Serializable;

public class Kelas implements Serializable {
    private String id;
    private String name;
    private String level;
    private String programStudy;
    private String capacity;
    private String description;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public Kelas() {}

    public Kelas(String id, String name, String programStudy, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.programStudy = programStudy;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Kelas(String id, String name, String level, String programStudy, String capacity, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.programStudy = programStudy;
        this.capacity = capacity;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getProgramStudy() { return programStudy; }
    public void setProgramStudy(String programStudy) { this.programStudy = programStudy; }

    public String getCapacity() { return capacity; }
    public void setCapacity(String capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name + " (" + programStudy + ")";
    }
}
