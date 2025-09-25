package com.example.androidphpmysql.models;

public class Borrowing {
    private int id;
    private String studentName;
    private String borrowDate;
    private String returnDate;

    // Constructor tanpa id (jika id tidak diperlukan)
    public Borrowing(String studentName, String borrowDate, String returnDate) {
        this.studentName = studentName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Constructor dengan id (jika id ikut dipakai)
    public Borrowing(int id, String studentName, String borrowDate, String returnDate) {
        this.id = id;
        this.studentName = studentName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    // Setter (opsional, kalau kamu mau ubah data nanti)
    public void setId(int id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
