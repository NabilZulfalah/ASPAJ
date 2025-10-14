package com.example.androidphpmysql;

import org.json.JSONArray;
import java.util.List;

public class Borrowing {
    private int id;
    private String status;
    private String borrowDate;
    private String returnDate;
    private String tujuan;
    private JSONArray items;
    private Student student;
    private List<BorrowingItem> itemsList;

    public Borrowing() {
    }

    public Borrowing(int id, String status, String borrowDate, String returnDate, String tujuan, JSONArray items) {
        this.id = id;
        this.status = status;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.tujuan = tujuan;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public JSONArray getItems() {
        return items;
    }

    public void setItems(List<BorrowingItem> itemsList) {
        this.itemsList = itemsList;
    }

    public List<BorrowingItem> getItemsList() {
        return itemsList;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
