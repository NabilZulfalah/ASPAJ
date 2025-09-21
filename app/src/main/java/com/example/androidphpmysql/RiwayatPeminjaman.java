package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class RiwayatPeminjaman extends AppCompatActivity {

        private Spinner spinnerStatus, spinnerJurusan, spinnerKelas;
        private EditText editTextSearchName;
        private RiwayatPeminjamanAdapter adapter;
        private final List<Borrowing> borrowingList = new ArrayList<>();

        private final List<String> statusList = new ArrayList<>();
        private final List<String> jurusanList = new ArrayList<>();
        private final List<String> kelasList = new ArrayList<>();

        // Toolbar & Drawer
        private Toolbar toolbar;
        private DrawerLayout drawerLayout;
        private NavigationView navigationView;

        // Wave
        private View headerWave, footerWave;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_riwayat_peminjaman);

                try {
                        // --- Wave animation ---
                        headerWave = findViewById(R.id.headerWave);
                        footerWave = findViewById(R.id.footerWave);

                        Animation topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_slide);
                        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_slide);

                        if (headerWave != null) headerWave.startAnimation(topAnimation);
                        if (footerWave != null) footerWave.startAnimation(bottomAnimation);

                        // --- Toolbar & Drawer ---
                        toolbar = findViewById(R.id.toolbar);
                        setSupportActionBar(toolbar);

                        drawerLayout = findViewById(R.id.drawerLayout);
                        navigationView = findViewById(R.id.navigationView);

                        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                                this,
                                drawerLayout,
                                toolbar,
                                R.string.navigation_drawer_open,
                                R.string.navigation_drawer_close
                        );
                        drawerLayout.addDrawerListener(toggle);
                        toggle.syncState();

                        navigationView.setNavigationItemSelectedListener(menuItem -> {
                                int id = menuItem.getItemId();
                                if (id == R.id.nav_home) {
                                        startActivity(new Intent(this, activity_main_menu.class));
                                } else if (id == R.id.nav_peminjam) {
                                        startActivity(new Intent(this, Peminjaman.class));
                                } else if (id == R.id.nav_riwayat) {
                                        startActivity(new Intent(this, RiwayatPeminjaman.class));
                                } else if (id == R.id.nav_pengembalian) {
                                        startActivity(new Intent(this, pengembalianActivity.class));
                                } else if (id == R.id.nav_logout) {
                                        getSharedPreferences("user_session", MODE_PRIVATE)
                                                .edit()
                                                .clear()
                                                .apply();
                                        Intent intent = new Intent(this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                }
                                drawerLayout.closeDrawers();
                                return true;
                        });

                        // --- RecyclerView + Filter setup ---
                        spinnerStatus = findViewById(R.id.spinnerStatus);
                        spinnerJurusan = findViewById(R.id.spinnerJurusan);
                        spinnerKelas = findViewById(R.id.spinnerKelas);
                        editTextSearchName = findViewById(R.id.editTextSearchName);
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewRiwayat);

                        recyclerView.setLayoutManager(
                                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        adapter = new RiwayatPeminjamanAdapter(borrowingList);
                        recyclerView.setAdapter(adapter);

                        // Hardcode kelas contoh
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

                        // Init dropdowns
                        initStatusDropdown();
                        fetchJurusan();
                        fetchKelas();

                        // Set listeners
                        spinnerJurusan.setOnItemSelectedListener(filterListener);
                        spinnerKelas.setOnItemSelectedListener(filterListener);

                        editTextSearchName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        fetchBorrowingHistory();
                                }

                                @Override
                                public void afterTextChanged(Editable s) {}
                        });

                        // Load data awal
                        fetchBorrowingHistory();

                } catch (Exception e) {
                        android.util.Log.e("RiwayatPeminjaman", "Error in onCreate", e);
                        Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
        }

        private void initStatusDropdown() {
                statusList.clear();
                statusList.add("Status");
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
                jurusanList.clear();
                jurusanList.add("Jurusan");
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
                // Untuk demo pakai hardcode, bisa diganti request API
                kelasList.clear();
                kelasList.add("Kelas");
                kelasList.add("X RPL 1");
                kelasList.add("XI TITL 1");
                kelasList.add("X DKV 2");
                kelasList.add("XII TAV 1");
                kelasList.add("XI TOI 2");
                kelasList.add("XII TKJ 3");

                ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        kelasList);
                kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKelas.setAdapter(kelasAdapter);
        }

        private final AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        fetchBorrowingHistory();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
        };

        private void fetchBorrowingHistory() {
                String statusFilter = spinnerStatus.getSelectedItem() != null
                        ? spinnerStatus.getSelectedItem().toString()
                        : "Status";
                String jurusanFilter = spinnerJurusan.getSelectedItem() != null
                        ? spinnerJurusan.getSelectedItem().toString()
                        : "Jurusan";
                String kelasFilter = spinnerKelas.getSelectedItem() != null
                        ? spinnerKelas.getSelectedItem().toString()
                        : "Kelas";
                String nameFilter = editTextSearchName.getText().toString().trim();

                loadMockData(statusFilter, jurusanFilter, kelasFilter, nameFilter);
        }

        // --- Model Borrowing ---
        public static class Borrowing {
                public int no;
                public String namaMurid;
                public String kelas;
                public String barangJumlah;
                public String tanggalJam;
                public String status;
                public String fotoProfile;
                public String tujuan;
                public String kondisiPengembalian;
                public String dikembalikanOleh;
                public String photo;
                public String aksi;


                public Borrowing(int no, String namaMurid, String kelas, String barangJumlah,
                                 String tanggalJam, String status) {
                        this.no = no;
                        this.namaMurid = namaMurid;
                        this.kelas = kelas;
                        this.barangJumlah = barangJumlah;
                        this.tanggalJam = tanggalJam;
                        this.status = status;
                }
        }

        private void loadMockData(String statusFilter, String jurusanFilter, String kelasFilter, String nameFilter) {
                borrowingList.clear();

                List<Borrowing> allData = new ArrayList<>();
                allData.add(new Borrowing(1, "Budi Santoso", "X RPL 1", "2 Laptop", "2025-09-18 10:00", "approved"));
                allData.add(new Borrowing(2, "Siti Aminah", "XI TITL 1", "1 Proyektor", "2025-09-17 09:30", "pending"));
                allData.add(new Borrowing(3, "Andi Wijaya", "X DKV 2", "1 Kamera DSLR", "2025-09-16 14:00", "returned"));
                allData.add(new Borrowing(4, "Rina Sari", "XII TAV 1", "2 Speaker", "2025-09-15 11:00", "approved"));

                for (Borrowing b : allData) {
                        boolean statusMatch = statusFilter.equals("Status") || b.status.equalsIgnoreCase(statusFilter);
                        boolean kelasMatch = kelasFilter.equals("Kelas") || b.kelas.equalsIgnoreCase(kelasFilter);
                        boolean nameMatch = nameFilter.isEmpty() || b.namaMurid.toLowerCase().contains(nameFilter.toLowerCase());

                        if (statusMatch && kelasMatch && nameMatch) {
                                borrowingList.add(b);
                        }
                }

                adapter.notifyDataSetChanged();
        }
}

