package com.example.androidphpmysql.models;

public class Borrowing {
    private int id;
    private String studentName;
    private String tujuan;
    private String kelas;
    private String borrowDate;
    private String returnDate;
    private String status;

    // Constructor
    public Borrowing(int id, String studentName, String tujuan, String kelas, String borrowDate, String returnDate, String status) {
        this.id = id;
        this.studentName = studentName;
        this.tujuan = tujuan;
        this.kelas = kelas;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTujuan() {
        return tujuan;
    }

    public String getKelas() {
        return kelas;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}