package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidphpmysql.SharedPrefManager;
import com.example.androidphpmysql.VolleySingleton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewClock;
    private TextView textViewActiveBorrowings;
    private TextView textViewTotalAssets;
    private Handler handler = new Handler();
    private Runnable runnable;

    private RecyclerView recyclerViewActiveBorrowings;
    private RecyclerView recyclerViewRecentRequests;
    private RecyclerView recyclerViewBorrowingHistory;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        textViewClock = findViewById(R.id.textViewClock);
        textViewActiveBorrowings = findViewById(R.id.textViewActiveBorrowings);
        textViewTotalAssets = findViewById(R.id.textViewTotalAssets);
        recyclerViewActiveBorrowings = findViewById(R.id.recyclerViewActiveBorrowings);
        recyclerViewRecentRequests = findViewById(R.id.recyclerViewRecentRequests);
        recyclerViewBorrowingHistory = findViewById(R.id.recyclerViewBorrowingHistory);

        // Set up RecyclerViews
        recyclerViewActiveBorrowings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBorrowingHistory.setLayoutManager(new LinearLayoutManager(this));

        // Start clock
        startClock();

        // Load data (placeholder for now)
        loadDashboardData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_borrow_assets) {
            startActivity(new Intent(this, SelectJurusanActivity.class));
        } else if (id == R.id.nav_my_borrowings) {
            startActivity(new Intent(this, BorrowingStatusActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, LoginActivity.class));
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

    private void startClock() {
        runnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                textViewClock.setText(sdf.format(new Date()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void loadDashboardData() {
        loadDashboardStats();
        loadActiveBorrowings();
        loadRecentRequests();
        loadBorrowingHistory();
    }

    private void loadDashboardStats() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.STUDENT_DASHBOARD_STATS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int activeBorrowings = response.getInt("active_borrowings");
                            int totalAssets = response.getInt("total_assets");
                            textViewActiveBorrowings.setText(String.valueOf(activeBorrowings));
                            textViewTotalAssets.setText(String.valueOf(totalAssets));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(StudentDashboardActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void loadActiveBorrowings() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Constants.STUDENT_ACTIVE_BORROWINGS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse and set adapter
                        // recyclerViewActiveBorrowings.setAdapter(new StudentBorrowingAdapter(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(StudentDashboardActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void loadRecentRequests() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Constants.STUDENT_RECENT_REQUESTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse and set adapter
                        // recyclerViewRecentRequests.setAdapter(new StudentBorrowingAdapter(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(StudentDashboardActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void loadBorrowingHistory() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Constants.STUDENT_BORROWING_HISTORY, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse and set adapter
                        // recyclerViewBorrowingHistory.setAdapter(new StudentBorrowingAdapter(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(StudentDashboardActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
