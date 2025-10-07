package com.example.androidphpmysql.models;

public class Borrowing {
    private int id;
    private String studentName;
    private String borrowDate;
    private String returnDate;
    private String status;
    private String tujuan;
    private String kelas;

    // Constructor lengkap
    public Borrowing(int id, String studentName, String borrowDate, String returnDate,
                     String status, String tujuan, String kelas) {
        this.id = id;
        this.studentName = studentName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
        this.tujuan = tujuan;
        this.kelas = kelas;
    }

    // Getter
    public int getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getBorrowDate() { return borrowDate; }
    public String getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public String getTujuan() { return tujuan; }
    public String getKelas() { return kelas; }
}
