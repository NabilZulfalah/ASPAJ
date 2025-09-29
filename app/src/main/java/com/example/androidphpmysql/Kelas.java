package com.example.androidphpmysql;

public class Kelas {
    private String id;
    private String namaKelas;
    private String tingkat;
    private String programStudi;
    private int jumlahSiswa;
    private int kapasitas;

    public Kelas() {
    }

    public Kelas(String id, String namaKelas, String tingkat, String programStudi, int jumlahSiswa, int kapasitas) {
        this.id = id;
        this.namaKelas = namaKelas;
        this.tingkat = tingkat;
        this.programStudi = programStudi;
        this.jumlahSiswa = jumlahSiswa;
        this.kapasitas = kapasitas;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getTingkat() {
        return tingkat;
    }

    public void setTingkat(String tingkat) {
        this.tingkat = tingkat;
    }

    public String getProgramStudi() {
        return programStudi;
    }

    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    public int getJumlahSiswa() {
        return jumlahSiswa;
    }

    public void setJumlahSiswa(int jumlahSiswa) {
        this.jumlahSiswa = jumlahSiswa;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    @Override
    public String toString() {
        return "Kelas{" +
                "id='" + id + '\'' +
                ", namaKelas='" + namaKelas + '\'' +
                ", tingkat='" + tingkat + '\'' +
                ", programStudi='" + programStudi + '\'' +
                ", jumlahSiswa=" + jumlahSiswa +
                ", kapasitas=" + kapasitas +
                '}';
    }
}
