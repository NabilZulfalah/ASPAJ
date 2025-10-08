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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMerk() {
        return merk;
    }

    public long getHarga_satuan() {
        return harga_satuan;
    }

    public String getSumber() {
        return sumber;
    }

    public int getTahun() {
        return tahun;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public int getStock() {
        return stock;
    }

    public String getCondition() {
        return condition;
    }

    public String getPhoto() {
        return photo;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getJurusan() {
        return jurusan;
    }
}
