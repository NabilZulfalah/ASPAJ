package com.example.androidphpmysql;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Arrays;
import java.util.List;

public class RiwayatPeminjaman extends AppCompatActivity {

        private Spinner spinnerStatus, spinnerJurusan, spinnerKelas;
        private EditText editTextSearchName;
        private RiwayatPeminjamanAdapter adapter;
        private final List<Borrowing> borrowingList = new ArrayList<>();

        private final List<String> statusList = new ArrayList<>();
        private final List<String> jurusanList = new ArrayList<>();
        private final List<String> kelasList = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                try {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.activity_riwayat_peminjaman);

                        spinnerStatus = findViewById(R.id.spinnerStatus);
                        spinnerJurusan = findViewById(R.id.spinnerJurusan);
                        spinnerKelas = findViewById(R.id.spinnerKelas);
                        editTextSearchName = findViewById(R.id.editTextSearchName);
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewRiwayat);

                        recyclerView.setLayoutManager(
                                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        adapter = new RiwayatPeminjamanAdapter(borrowingList);
                        recyclerView.setAdapter(adapter);

                        List<String> testData = new ArrayList<>();
                        testData.add("Semua Kelas");
                        testData.add("X RPL 1");
                        testData.add("XI TITL 1");
                        testData.add("X DKV 2");
                        testData.add("XII TAV 1");
                        testData.add("XI TOI 2");
                        testData.add("XII TKJ 3");
                        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this,
                                        android.R.layout.simple_spinner_item,
                                        testData);
                        kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKelas.setAdapter(kelasAdapter);

                        // Initialize dropdowns
                        initStatusDropdown();
                        fetchJurusan();
                        fetchKelas();

                        // Set listeners to refresh data on filter change
                        spinnerStatus.setOnItemSelectedListener(filterListener);
                        spinnerJurusan.setOnItemSelectedListener(filterListener);
                        spinnerKelas.setOnItemSelectedListener(filterListener);

                        editTextSearchName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        fetchBorrowingHistory();
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                        });

                        // Initial data load
                        fetchBorrowingHistory();
                } catch (Exception e) {
                        android.util.Log.e("RiwayatPeminjaman", "Error in onCreate", e);
                        Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                }
        }

        private void initStatusDropdown() {
                // Hardcoded status options based on database enum
                statusList.clear();
                statusList.add("Semua Status");
                statusList.add("pending");
                statusList.add("approved");
                statusList.add("rejected");
                statusList.add("returned");

                ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_dropdown_item,
                                statusList);
                spinnerStatus.setAdapter(statusAdapter);
        }

        private void fetchJurusan() {
                // For simplicity, hardcode jurusan based on commodities table distinct jurusan
                // values
                jurusanList.clear();
                jurusanList.add("Semua Jurusan");
                jurusanList.add("Rekayasa Perangkat Lunak");
                jurusanList.add("Teknik Instalasi Tenaga Listrik");
                jurusanList.add("Desain Komunikasi Visual");
                jurusanList.add("Teknik Audio Video");
                jurusanList.add("Teknik Otomasi Industri");
                jurusanList.add("Teknik Komputer Jaringan");

                ArrayAdapter<String> jurusanAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                jurusanList);
                jurusanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerJurusan.setAdapter(jurusanAdapter);
        }

        private void fetchKelas() {
                String url = "http://192.168.4.123/ASPAJ/v1/get_kelas.php";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                response -> {
                                        Toast.makeText(RiwayatPeminjaman.this, "Response: " + response,
                                                        Toast.LENGTH_LONG).show();
                                        kelasList.clear();
                                        try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                kelasList.add("Semua Kelas");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                        String kelas = jsonArray.getString(i);
                                                        kelasList.add(kelas);
                                                        android.util.Log.d("RiwayatPeminjaman",
                                                                        "Kelas added: " + kelas);
                                                }
                                                ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this,
                                                                android.R.layout.simple_spinner_item, kelasList);
                                                kelasAdapter.setDropDownViewResource(
                                                                android.R.layout.simple_spinner_dropdown_item);
                                                spinnerKelas.setAdapter(kelasAdapter);
                                        } catch (Exception e) {
                                                Toast.makeText(RiwayatPeminjaman.this, "Error parsing kelas data",
                                                                Toast.LENGTH_SHORT).show();
                                                android.util.Log.e("RiwayatPeminjaman", "Error parsing kelas data", e);
                                        }
                                },
                                error -> {
                                        Toast.makeText(RiwayatPeminjaman.this,
                                                        "Error fetching kelas: " + error.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                        android.util.Log.e("RiwayatPeminjaman", "Error fetching kelas", error);
                                });
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
                final String statusFilter = spinnerStatus.getSelectedItem() != null
                                ? spinnerStatus.getSelectedItem().toString()
                                : "Semua Status";
                final String jurusanFilter = spinnerJurusan.getSelectedItem() != null
                                ? spinnerJurusan.getSelectedItem().toString()
                                : "Semua Jurusan";
                final String kelasFilter = spinnerKelas.getSelectedItem() != null
                                ? spinnerKelas.getSelectedItem().toString()
                                : "Semua Kelas";
                final String nameFilter = editTextSearchName.getText().toString().trim();

                String url = "http://192.168.4.123/ASPAJ/v1/get_borrowings.php?status=" + statusFilter + "&jurusan="
                                + jurusanFilter + "&kelas=" + kelasFilter + "&name=" + nameFilter;

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
                                                                        obj.optString("aksi", ""));
                                                        borrowingList.add(borrowing);
                                                }
                                                adapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                                Toast.makeText(RiwayatPeminjaman.this, "Error parsing borrowing data",
                                                                Toast.LENGTH_SHORT).show();
                                                android.util.Log.e("RiwayatPeminjaman", "Error parsing borrowing data",
                                                                e);
                                        }
                                },
                                error -> {
                                        android.util.Log.e("RiwayatPeminjaman", "Error fetching borrowing data", error);
                                        String message = error.getMessage();
                                        if (message == null)
                                                message = "Unknown error";
                                        Toast.makeText(RiwayatPeminjaman.this,
                                                        "Error fetching borrowing data: " + message,
                                                        Toast.LENGTH_SHORT).show();
                                });
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

                public Borrowing(int no, String fotoProfile, String namaMurid, String kelas, String barangJumlah,
                                String tujuan,
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
