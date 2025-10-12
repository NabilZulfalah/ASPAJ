package com.example.androidphpmysql;

public class SchoolClass {
    private int id;
    private String name;
    private String level;
    private String programStudy;
    private int capacity;
    private String description;

    public SchoolClass() {
    }

    public SchoolClass(int id, String name, String level, String programStudy, int capacity, String description) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.programStudy = programStudy;
        this.capacity = capacity;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getProgramStudy() {
        return programStudy;
    }

    public void setProgramStudy(String programStudy) {
        this.programStudy = programStudy;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
