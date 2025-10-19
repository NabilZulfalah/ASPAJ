package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard utama untuk siswa.
 * Menampilkan statistik aset, peminjaman aktif, dan riwayat.
 */
public class StudentDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // ===============================
    // 🔹 View Components
    // ===============================
    private TextView textViewClock, textViewActiveBorrowings, textViewTotalAssets;
    private RecyclerView recyclerViewActiveBorrowings, recyclerViewRecentRequests, recyclerViewBorrowingHistory;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // ===============================
    // 🔹 Clock
    // ===============================
    private Handler handler;
    private Runnable clockRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // === Toolbar & Drawer ===
        setupDrawer();

        // === View Binding ===
        bindViews();

        // === RecyclerView setup ===
        setupRecyclerViews();

        // === Start Clock ===
        startClock();

        // === Load Dashboard Data ===
        loadDashboardData();
    }

    // ===============================
    // 🔹 Drawer Setup
    // ===============================
    private void setupDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    // ===============================
    // 🔹 View Binding
    // ===============================
    private void bindViews() {
        textViewClock = findViewById(R.id.textViewClock);
        textViewActiveBorrowings = findViewById(R.id.textViewActiveBorrowings);
        textViewTotalAssets = findViewById(R.id.textViewTotalAssets);

        recyclerViewActiveBorrowings = findViewById(R.id.recyclerViewActiveBorrowings);
        recyclerViewRecentRequests = findViewById(R.id.recyclerViewRecentRequests);
        recyclerViewBorrowingHistory = findViewById(R.id.recyclerViewBorrowingHistory);
    }

    // ===============================
    // 🔹 RecyclerView Setup
    // ===============================
    private void setupRecyclerViews() {
        recyclerViewActiveBorrowings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBorrowingHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    // ===============================
    // 🔹 Navigation Drawer Actions
    // ===============================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Stay on dashboard
        } else if (id == R.id.nav_borrow_assets) {
            startActivity(new Intent(this, SelectJurusanActivity.class));
        } else if (id == R.id.nav_my_borrowings) {
            startActivity(new Intent(this, BorrowingStatusActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).logout();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ===============================
    // 🔹 Real-Time Clock
    // ===============================
    private void startClock() {
        handler = new Handler();
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                textViewClock.setText(sdf.format(new Date()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(clockRunnable);
    }

    // ===============================
    // 🔹 Load Dashboard Data
    // ===============================
    private void loadDashboardData() {
        loadDashboardStats();
        loadActiveBorrowings();
        loadRecentRequests();
        loadBorrowingHistory();
    }

    // -------------------------------
    // Dashboard Stats
    // -------------------------------
    private void loadDashboardStats() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constants.STUDENT_DASHBOARD_STATS,
                null,
                response -> {
                    int activeBorrowings = response.optInt("active_borrowings", 0);
                    int totalAssets = response.optInt("total_assets", 0);

                    textViewActiveBorrowings.setText(String.valueOf(activeBorrowings));
                    textViewTotalAssets.setText(String.valueOf(totalAssets));
                },
                error -> Toast.makeText(this, "Gagal memuat statistik dashboard", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // -------------------------------
    // Active Borrowings
    // -------------------------------
    private void loadActiveBorrowings() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Constants.STUDENT_ACTIVE_BORROWINGS,
                null,
                response -> handleBorrowingResponse(response, "active"),
                error -> Toast.makeText(this, "Gagal memuat peminjaman aktif", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // -------------------------------
    // Recent Requests
    // -------------------------------
    private void loadRecentRequests() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Constants.STUDENT_RECENT_REQUESTS,
                null,
                response -> handleBorrowingResponse(response, "recent"),
                error -> Toast.makeText(this, "Gagal memuat permintaan terbaru", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // -------------------------------
    // Borrowing History
    // -------------------------------
    private void loadBorrowingHistory() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Constants.STUDENT_BORROWING_HISTORY,
                null,
                response -> handleBorrowingResponse(response, "history"),
                error -> Toast.makeText(this, "Gagal memuat riwayat peminjaman", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeaders();
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // ===============================
    // 🔹 Helper Methods
    // ===============================
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null) {
            headers.put("Authorization", "Bearer " + token);
        }
        return headers;
    }

    private void handleBorrowingResponse(JSONArray response, String type) {
        List<Borrowing> borrowings = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Borrowing borrowing = new Borrowing(
                        obj.getInt("id"),
                        obj.getString("borrow_date"),
                        obj.getString("return_date"),
                        obj.getString("tujuan"),
                        obj.getString("status"),
                        obj.getJSONArray("items")
                );
                borrowings.add(borrowing);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (type) {
            case "active":
                BorrowingCardAdapter activeAdapter = new BorrowingCardAdapter(this, borrowings);
                recyclerViewActiveBorrowings.setAdapter(activeAdapter);
                break;
            case "recent":
                BorrowingCardAdapter recentAdapter = new BorrowingCardAdapter(this, borrowings);
                recyclerViewRecentRequests.setAdapter(recentAdapter);
                break;
            case "history":
                BorrowingCardAdapter historyAdapter = new BorrowingCardAdapter(this, borrowings);
                recyclerViewBorrowingHistory.setAdapter(historyAdapter);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && clockRunnable != null) {
            handler.removeCallbacks(clockRunnable);
        }
    }
}
