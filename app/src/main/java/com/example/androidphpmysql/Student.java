package com.example.androidphpmysql;

public class Student {
    private int id;
    private String name;
    private String nim;
    private SchoolClass schoolClass;
    private User user;

    public Student() {
    }

    public Student(int id, String name, String nim, SchoolClass schoolClass, User user) {
        this.id = id;
        this.name = name;
        this.nim = nim;
        this.schoolClass = schoolClass;
        this.user = user;
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

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
