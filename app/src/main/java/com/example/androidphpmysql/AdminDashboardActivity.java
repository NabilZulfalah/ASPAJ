package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewTotalAssets;
    private TextView textViewTotalUsers;
    private TextView textViewPendingBorrowings;
    private TextView textViewActiveBorrowings;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        textViewTotalAssets = findViewById(R.id.textViewTotalAssets);
        textViewTotalUsers = findViewById(R.id.textViewTotalUsers);
        textViewPendingBorrowings = findViewById(R.id.textViewPendingBorrowings);
        textViewActiveBorrowings = findViewById(R.id.textViewActiveBorrowings);

        // Load dashboard stats
        loadDashboardStats();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_asset_management) {
            startActivity(new Intent(this, AssetListActivity.class));
        } else if (id == R.id.nav_user_management) {
            startActivity(new Intent(this, UserListActivity.class));
        } else if (id == R.id.nav_borrowing_management) {
            startActivity(new Intent(this, OfficerBorrowingManagementActivity.class));
        } else if (id == R.id.nav_class_management) {
            startActivity(new Intent(this, KelasListActivity.class));
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

    private void loadDashboardStats() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.ADMIN_DASHBOARD_STATS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalAssets = response.optInt("total_assets", 0);
                            int totalUsers = response.optInt("total_users", 0);
                            int pendingBorrowings = response.optInt("pending_borrowings", 0);
                            int activeBorrowings = response.optInt("active_borrowings", 0);

                            textViewTotalAssets.setText(String.valueOf(totalAssets));
                            textViewTotalUsers.setText(String.valueOf(totalUsers));
                            textViewPendingBorrowings.setText(String.valueOf(pendingBorrowings));
                            textViewActiveBorrowings.setText(String.valueOf(activeBorrowings));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            try {
                                String responseBody = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                                JSONObject data = new JSONObject(responseBody);
                                if (data.has("message")) {
                                    String errorMessage = data.getString("message");
                                    android.util.Log.e("AdminDashboardActivity", "Laravel Error: " + errorMessage, error);
                                }
                            } catch (Exception e) {
                                android.util.Log.e("AdminDashboardActivity", "Error parsing error response", e);
                            }
                        }
                        android.util.Log.e("AdminDashboardActivity", "Volley error", error);
                        // Handle error, perhaps set defaults or show toast
                    }
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(AdminDashboardActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
