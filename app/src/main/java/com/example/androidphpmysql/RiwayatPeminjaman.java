package com.example.androidphpmysql;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatPeminjaman extends AppCompatActivity {

    private Spinner spinnerStatus, spinnerJurusan, spinnerKelas;
    private RiwayatPeminjamanAdapter adapter;
    private final List<Borrowing> borrowingList = new ArrayList<>();

    private final List<String> statusList = new ArrayList<>();
    private final List<String> jurusanList = new ArrayList<>();
    private final List<String> kelasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_peminjaman);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerJurusan = findViewById(R.id.spinnerJurusan);
        spinnerKelas = findViewById(R.id.spinnerKelas);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRiwayat);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new RiwayatPeminjamanAdapter(borrowingList);
        recyclerView.setAdapter(adapter);

        // Initialize dropdowns
        initStatusDropdown();
        fetchJurusan();
        fetchKelas();

        // Set listeners to refresh data on filter change
        spinnerStatus.setOnItemSelectedListener(filterListener);
        spinnerJurusan.setOnItemSelectedListener(filterListener);
        spinnerKelas.setOnItemSelectedListener(filterListener);

        // Initial data load
        fetchBorrowingHistory();
    }

    private void initStatusDropdown() {
        // Hardcoded status options based on database enum
        statusList.clear();
        statusList.add("Semua Status");
        statusList.add("pending");
        statusList.add("approved");
        statusList.add("rejected");
        statusList.add("returned");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void fetchJurusan() {
        // For simplicity, hardcode jurusan based on commodities table distinct jurusan values
        jurusanList.clear();
        jurusanList.add("Semua Jurusan");
        jurusanList.add("Rekayasa Perangkat Lunak");
        jurusanList.add("Teknik Instalasi Tenaga Listrik");
        jurusanList.add("Desain Komunikasi Visual");
        jurusanList.add("Teknik Audio Video");
        jurusanList.add("Teknik Otomasi Industri");
        jurusanList.add("Teknik Komputer Jaringan");

        ArrayAdapter<String> jurusanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, jurusanList);
        spinnerJurusan.setAdapter(jurusanAdapter);
    }

    private void fetchKelas() {
        String url = "http://172.16.100.120/ASPAJ/v1/get_kelas.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    kelasList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        kelasList.add("Semua Kelas");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            kelasList.add(jsonArray.getString(i));
                        }
                        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kelasList);
                        spinnerKelas.setAdapter(kelasAdapter);
                    } catch (Exception e) {
                        Toast.makeText(RiwayatPeminjaman.this, "Error parsing kelas data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(RiwayatPeminjaman.this, "Error fetching kelas: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private final AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            fetchBorrowingHistory();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void fetchBorrowingHistory() {
        final String statusFilter = spinnerStatus.getSelectedItem() != null ? spinnerStatus.getSelectedItem().toString() : "Semua Status";
        final String jurusanFilter = spinnerJurusan.getSelectedItem() != null ? spinnerJurusan.getSelectedItem().toString() : "Semua Jurusan";
        final String kelasFilter = spinnerKelas.getSelectedItem() != null ? spinnerKelas.getSelectedItem().toString() : "Semua Kelas";

        String url = "http://172.16.100.120/ASPAJ/v1/get_borrowings.php?status=" + statusFilter + "&jurusan=" + jurusanFilter + "&kelas=" + kelasFilter;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    borrowingList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Borrowing borrowing = new Borrowing(
                                    obj.optInt("no", i + 1),
                                    obj.optString("foto_profile", ""),
                                    obj.optString("nama_murid", ""),
                                    obj.optString("kelas", ""),
                                    obj.optString("barang_jumlah", ""),
                                    obj.optString("tujuan", ""),
                                    obj.optString("tanggal_jam", ""),
                                    obj.optString("status", ""),
                                    obj.optString("kondisi_pengembalian", ""),
                                    obj.optString("dikembalikan_oleh", ""),
                                    obj.optString("photo", ""),
                                    obj.optString("aksi", "")
                            );
                            borrowingList.add(borrowing);
                        }
                        adapter.notifyItemRangeInserted(0, borrowingList.size());
                    } catch (Exception e) {
                        Toast.makeText(RiwayatPeminjaman.this, "Error parsing borrowing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(RiwayatPeminjaman.this, "Error fetching borrowing data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Data model class
    public static class Borrowing {
        public int no;
        public String fotoProfile;
        public String namaMurid;
        public String kelas;
        public String barangJumlah;
        public String tujuan;
        public String tanggalJam;
        public String status;
        public String kondisiPengembalian;
        public String dikembalikanOleh;
        public String photo;
        public String aksi;

        public Borrowing(int no, String fotoProfile, String namaMurid, String kelas, String barangJumlah, String tujuan,
                         String tanggalJam, String status, String kondisiPengembalian, String dikembalikanOleh,
                         String photo, String aksi) {
            this.no = no;
            this.fotoProfile = fotoProfile;
            this.namaMurid = namaMurid;
            this.kelas = kelas;
            this.barangJumlah = barangJumlah;
            this.tujuan = tujuan;
            this.tanggalJam = tanggalJam;
            this.status = status;
            this.kondisiPengembalian = kondisiPengembalian;
            this.dikembalikanOleh = dikembalikanOleh;
            this.photo = photo;
            this.aksi = aksi;
        }
    }
}
