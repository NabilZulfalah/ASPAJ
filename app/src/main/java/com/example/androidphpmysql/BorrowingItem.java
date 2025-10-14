package com.example.androidphpmysql;

public class BorrowingItem {
    private int id;
    private String code;
    private String name;
    private String status;
    private int quantity;
    private String stockInfo;
    private String photoUrl;
    private Commodity commodity;

    public BorrowingItem() {
    }

    public BorrowingItem(String id, String code, String name, String status, int quantity, String stockInfo, String photoUrl) {
        this.id = Integer.parseInt(id);
        this.code = code;
        this.name = name;
        this.status = status;
        this.quantity = quantity;
        this.stockInfo = stockInfo;
        this.photoUrl = photoUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public String getStockInfo() { return stockInfo; }
    public String getPhotoUrl() { return photoUrl; }
    public Commodity getCommodity() { return commodity; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStockInfo(String stockInfo) { this.stockInfo = stockInfo; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
}
