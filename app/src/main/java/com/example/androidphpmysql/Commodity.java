package com.example.androidphpmysql;

public class Commodity {
    private int id;
    private String code;
    private String name;
    private String merk;
    private long harga_satuan;
    private String sumber;
    private int tahun;
    private String deskripsi;
    private int stock;
    private String condition;
    private String photo;
    private String lokasi;
    private String jurusan;

    public Commodity() {
    }

    public Commodity(int id, String code, String name, String merk, long harga_satuan, String sumber, int tahun, String deskripsi, int stock, String condition, String photo, String lokasi, String jurusan) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.merk = merk;
        this.harga_satuan = harga_satuan;
        this.sumber = sumber;
        this.tahun = tahun;
        this.deskripsi = deskripsi;
        this.stock = stock;
        this.condition = condition;
        this.photo = photo;
        this.lokasi = lokasi;
        this.jurusan = jurusan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public long getHarga_satuan() {
        return harga_satuan;
    }

    public void setHarga_satuan(long harga_satuan) {
        this.harga_satuan = harga_satuan;
    }

    public String getSumber() {
        return sumber;
    }

    public void setSumber(String sumber) {
        this.sumber = sumber;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }
}
