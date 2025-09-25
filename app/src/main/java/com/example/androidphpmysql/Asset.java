package com.example.androidphpmysql;

public class Asset {
    private int id;
    private String namaBarang;
    private String kodeBarang;
    private String jumlahStok;
    private String lokasiBarang;
    private String jurusanBarang;
    private String merk;
    private double hargaSatuan;
    private String sumber;
    private String tahun;
    private String deskripsi;

    public Asset() {
    }

    public Asset(int id, String namaBarang, String kodeBarang, String jumlahStok, String lokasiBarang,
                 String jurusanBarang, String merk, double hargaSatuan, String sumber, String tahun, String deskripsi) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.kodeBarang = kodeBarang;
        this.jumlahStok = jumlahStok;
        this.lokasiBarang = lokasiBarang;
        this.jurusanBarang = jurusanBarang;
        this.merk = merk;
        this.hargaSatuan = hargaSatuan;
        this.sumber = sumber;
        this.tahun = tahun;
        this.deskripsi = deskripsi;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getJumlahStok() {
        return jumlahStok;
    }

    public void setJumlahStok(String jumlahStok) {
        this.jumlahStok = jumlahStok;
    }

    public String getLokasiBarang() {
        return lokasiBarang;
    }

    public void setLokasiBarang(String lokasiBarang) {
        this.lokasiBarang = lokasiBarang;
    }

    public String getJurusanBarang() {
        return jurusanBarang;
    }

    public void setJurusanBarang(String jurusanBarang) {
        this.jurusanBarang = jurusanBarang;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public void setHargaSatuan(double hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public String getSumber() {
        return sumber;
    }

    public void setSumber(String sumber) {
        this.sumber = sumber;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // For backward compatibility with list display
    public String getName() {
        return namaBarang;
    }

    public String getDescription() {
        return deskripsi;
    }

    public String getCategory() {
        return jurusanBarang;
    }

    public double getValue() {
        return hargaSatuan;
    }

    // Add setters for backward compatibility
    public void setName(String name) {
        this.namaBarang = name;
    }

    public void setDescription(String description) {
        this.deskripsi = description;
    }

    public void setCategory(String category) {
        this.jurusanBarang = category;
    }

    public void setValue(double value) {
        this.hargaSatuan = value;
    }
}
