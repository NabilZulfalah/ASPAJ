package com.example.androidphpmysql.model;

public class BorrowingItem {
    private Commodity commodity;
    private String status;
    private int quantity;
    private boolean canReturn;
    private String returnUrl;

    public Commodity getCommodity() { return commodity; }
    public String getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public boolean isCanReturn() { return canReturn; }
    public String getReturnUrl() { return returnUrl; }

    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public void setStatus(String status) { this.status = status; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCanReturn(boolean canReturn) { this.canReturn = canReturn; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
}
