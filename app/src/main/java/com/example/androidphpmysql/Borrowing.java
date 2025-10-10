package com.example.androidphpmysql;

import org.json.JSONArray;

public class Borrowing {
    private int id;
    private String status;
    private String borrowDate;
    private String returnDate;
    private String tujuan;
    private JSONArray items;

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

    public String getStatus() {
        return status;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getTujuan() {
        return tujuan;
    }

    public JSONArray getItems() {
        return items;
    }
}
