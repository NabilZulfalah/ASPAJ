package com.example.androidphpmysql;

public class SchoolClass {
    private int id;
    private String name;
    private String level;
    private String programStudy;
    private int capacity;
    private String description;

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

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getProgramStudy() {
        return programStudy;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }
}
