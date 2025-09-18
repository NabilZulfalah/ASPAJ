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

                // For demo, always load and filter mock data
                loadMockData(statusFilter, jurusanFilter, kelasFilter, nameFilter);

                // Uncomment below to use API
                /*
                 * String url = "http://192.168.4.123/ASPAJ/v1/get_borrowings.php?status=" +
                 * statusFilter + "&jurusan="
                 * + jurusanFilter + "&kelas=" + kelasFilter + "&name=" + nameFilter;
                 * 
                 * StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                 * response -> {
                 * borrowingList.clear();
                 * try {
                 * JSONArray jsonArray = new JSONArray(response);
                 * for (int i = 0; i < jsonArray.length(); i++) {
                 * JSONObject obj = jsonArray.getJSONObject(i);
                 * Borrowing borrowing = new Borrowing(
                 * obj.optInt("no", i + 1),
                 * obj.optString("foto_profile", ""),
                 * obj.optString("nama_murid", ""),
                 * obj.optString("kelas", ""),
                 * obj.optString("barang_jumlah", ""),
                 * obj.optString("tujuan", ""),
                 * obj.optString("tanggal_jam", ""),
                 * obj.optString("status", ""),
                 * obj.optString("kondisi_pengembalian", ""),
                 * obj.optString("dikembalikan_oleh", ""),
                 * obj.optString("photo", ""),
                 * obj.optString("aksi", ""));
                 * borrowingList.add(borrowing);
                 * }
                 * adapter.notifyItemRangeInserted(0, borrowingList.size());
                 * } catch (Exception e) {
                 * Toast.makeText(RiwayatPeminjaman.this, "Error parsing borrowing data",
                 * Toast.LENGTH_SHORT)
                 * .show();
                 * }
                 * },
                 * error -> {
                 * android.util.Log.e("RiwayatPeminjaman", "Error fetching borrowing data",
                 * error);
                 * String message = error.getMessage();
                 * if (message == null)
                 * message = "Unknown error";
                 * Toast.makeText(RiwayatPeminjaman.this,
                 * "Error fetching borrowing data: " + message + ". Loading mock data.",
                 * Toast.LENGTH_SHORT).show();
                 * 
                 * // Load mock data as fallback
                 * loadMockData(statusFilter, jurusanFilter, kelasFilter, nameFilter);
                 * });
                 * Volley.newRequestQueue(this).add(stringRequest);
                 */
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

        private boolean matchesJurusan(String jurusanFilter, String barangJumlah) {
                switch (jurusanFilter) {
                        case "Rekayasa Perangkat Lunak":
                                return barangJumlah.contains("Laptop");
                        case "Teknik Instalasi Tenaga Listrik":
                                return barangJumlah.contains("Proyektor");
                        case "Desain Komunikasi Visual":
                                return barangJumlah.contains("Kamera DSLR");
                        case "Teknik Audio Video":
                                return barangJumlah.contains("Speaker");
                        case "Teknik Otomasi Industri":
                                return barangJumlah.contains("PLC Kit");
                        case "Teknik Komputer Jaringan":
                                return barangJumlah.contains("Router");
                        default:
                                return false;
                }
        }

        private void loadMockData(String statusFilter, String jurusanFilter, String kelasFilter, String nameFilter) {
                borrowingList.clear();
                // Mock data with various statuses, jurusans, kelas
                List<Borrowing> allData = new ArrayList<>();
                allData.add(new Borrowing(1, "", "Budi Santoso", "X RPL 1", "2 Laptop", "Praktikum RPL",
                                "2025-09-18 10:00", "approved", "Baik", "Guru A", "", "Lihat"));
                allData.add(new Borrowing(2, "", "Siti Aminah", "XI TITL 1", "1 Proyektor", "Proyek TITL",
                                "2025-09-17 09:30", "pending", "", "", "", "Lihat"));
                allData.add(new Borrowing(3, "", "Andi Wijaya", "X DKV 2", "1 Kamera DSLR", "Fotografi",
                                "2025-09-16 14:00", "returned", "Baik", "Guru B", "", "Lihat"));
                allData.add(new Borrowing(4, "", "Rina Sari", "XII TAV 1", "2 Speaker", "Audio Recording",
                                "2025-09-15 11:00", "approved", "Sedang", "Guru C", "", "Lihat"));
                allData.add(new Borrowing(5, "", "Dedi Kurniawan", "XI TOI 2", "1 PLC Kit", "Otomasi",
                                "2025-09-14 08:00",
                                "rejected", "", "", "", "Lihat"));
                allData.add(new Borrowing(6, "", "Maya Putri", "XII TKJ 3", "3 Router", "Jaringan",
                                "2025-09-13 13:00",
                                "pending", "", "", "", "Lihat"));
                allData.add(
                                new Borrowing(7, "", "Ahmad Fauzi", "X RPL 1", "1 Laptop", "Coding", "2025-09-12 15:00",
                                                "returned", "Rusak", "Guru D", "", "Lihat"));
                allData.add(new Borrowing(8, "", "Lina Marlina", "XI TITL 1", "1 Proyektor", "Presentasi",
                                "2025-09-11 10:30", "approved", "Baik", "Guru E", "", "Lihat"));
                allData.add(new Borrowing(9, "", "Rudi Hartono", "X DKV 2", "1 Kamera DSLR", "Video",
                                "2025-09-10 12:00",
                                "pending", "", "", "", "Lihat"));
                allData.add(new Borrowing(10, "", "Sari Dewi", "XII TAV 1", "1 Speaker", "Sound Test",
                                "2025-09-09 09:00",
                                "returned", "Baik", "Guru F", "", "Lihat"));
                allData.add(new Borrowing(11, "", "Tono Suryo", "XI TOI 2", "1 PLC Kit", "Automation",
                                "2025-09-08 14:30",
                                "approved", "Baik", "Guru G", "", "Lihat"));
                allData.add(new Borrowing(12, "", "Nina Kartika", "XII TKJ 3", "2 Router", "Network Setup",
                                "2025-09-07 11:15", "rejected", "", "", "", "Lihat"));
                allData.add(new Borrowing(13, "", "Bambang Setiawan", "X RPL 1", "1 Laptop", "Web Dev",
                                "2025-09-06 16:00", "pending", "", "", "", "Lihat"));
                allData.add(new Borrowing(14, "", "Yuni Susanti", "XI TITL 1", "1 Proyektor", "Demo",
                                "2025-09-05 10:45",
                                "returned", "Baik", "Guru H", "", "Lihat"));
                allData.add(new Borrowing(15, "", "Eko Prasetyo", "X DKV 2", "1 Kamera DSLR", "Photography",
                                "2025-09-04 13:30", "approved", "Sedang", "Guru I", "", "Lihat"));
                allData.add(new Borrowing(16, "", "Fajar Setiawan", "X RPL 1", "1 Router", "Network Setup",
                                "2025-09-03 14:00", "pending", "", "", "", "Lihat"));

                for (Borrowing b : allData) {
                        boolean statusMatch = statusFilter.equals("Semua Status")
                                        || b.status.equalsIgnoreCase(statusFilter);
                        boolean jurusanMatch = jurusanFilter.equals("Semua Jurusan")
                                        || matchesJurusan(jurusanFilter, b.barangJumlah);
                        boolean kelasMatch = kelasFilter.equals("Semua Kelas") || b.kelas.equalsIgnoreCase(kelasFilter);
                        boolean nameMatch = nameFilter.isEmpty() ||
                                b.namaMurid.toLowerCase().contains(nameFilter.toLowerCase());
                        if (statusMatch && jurusanMatch && kelasMatch && nameMatch) {
                                borrowingList.add(b);
                        }
                }
                adapter.notifyDataSetChanged();
        }
}
