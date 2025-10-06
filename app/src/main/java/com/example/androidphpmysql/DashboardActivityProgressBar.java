package com.example.androidphpmysql;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.adapters.PendingBorrowingsAdapter;
import com.example.androidphpmysql.models.Borrowing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivityProgressBar extends AppCompatActivity {

    private TextView textViewWelcome;
    private TextView textViewPeminjamAktif, textViewTotalAset, textViewMenungguPersetujuan, textViewBelumDikembalikan;
    private RecyclerView recyclerViewPending;
    private ProgressBar progressBar;

    private PendingBorrowingsAdapter adapter;
    private List<Borrowing> pendingBorrowingsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewPeminjamAktif = findViewById(R.id.textViewPeminjamAktif);
        textViewTotalAset = findViewById(R.id.textViewTotalAset);
        textViewMenungguPersetujuan = findViewById(R.id.textViewMenungguPersetujuan);
        textViewBelumDikembalikan = findViewById(R.id.textViewBelumDikembalikan);
        recyclerViewPending = findViewById(R.id.recyclerViewPending);
        progressBar = findViewById(R.id.progressBar);

        // Set welcome message with username
        String username = SharedPrefManager.getInstance(this).getUsername();
        textViewWelcome.setText("Selamat datang user " + username + " di peminjaman aset");

        // Setup RecyclerView
        pendingBorrowingsList = new ArrayList<>();
        adapter = new PendingBorrowingsAdapter(pendingBorrowingsList);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setAdapter(adapter);

        // Setup approve/reject listeners
        adapter.setOnApproveRejectListener(new PendingBorrowingsAdapter.OnApproveRejectListener() {
            @Override
            public void onApproveClick(Borrowing borrowing, int position) {
                updateBorrowingStatus(borrowing.getId(), "approved", position);
            }

            @Override
            public void onRejectClick(Borrowing borrowing, int position) {
                updateBorrowingStatus(borrowing.getId(), "rejected", position);
            }
        });

        // Load data
        loadDashboardData();
        loadPendingBorrowings();
    }

    private void loadDashboardData() {
        // For now, set static values or load from API
        // You can implement API calls for dashboard statistics if needed
        textViewPeminjamAktif.setText("0");
        textViewTotalAset.setText("0");
        textViewMenungguPersetujuan.setText("0");
        textViewBelumDikembalikan.setText("0");
    }

    private void loadPendingBorrowings() {
        showLoading();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_PENDING_BORROWINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoading();
                        Log.d("DashboardActivity", "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                pendingBorrowingsList.clear();

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject borrowingObj = dataArray.getJSONObject(i);

                                    int id = borrowingObj.getInt("id");
                                    String studentName = borrowingObj.getString("student_name");
                                    String borrowDate = borrowingObj.optString("borrow_date", "-");
                                    String returnDate = borrowingObj.optString("return_date", "-");
                                    String status = borrowingObj.optString("status", "pending");
                                    String tujuan = borrowingObj.optString("tujuan", "-");
                                    String kelas = borrowingObj.optString("class", "-");

                                    Borrowing borrowing = new Borrowing(id, studentName, borrowDate, returnDate, status, tujuan, kelas);
                                    pendingBorrowingsList.add(borrowing);
                                }

                                adapter.notifyDataSetChanged();
                                textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                            } else {
                                Toast.makeText(DashboardActivityProgressBar.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("DashboardActivity", "JSON Error: " + e.getMessage());
                            Toast.makeText(DashboardActivityProgressBar.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        Log.e("DashboardActivity", "Volley Error: " + error.getMessage());
                        Toast.makeText(DashboardActivityProgressBar.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateBorrowingStatus(int borrowingId, String status, int position) {
        showLoading();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoading();
                        Log.d("DashboardActivity", "Update Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("success")) {
                                Toast.makeText(DashboardActivityProgressBar.this,
                                    status.equals("approved") ? "Peminjaman disetujui" : "Peminjaman ditolak",
                                    Toast.LENGTH_SHORT).show();

                                // Remove item from list and update UI
                                adapter.removeItem(position);
                                textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                            } else {
                                Toast.makeText(DashboardActivityProgressBar.this,
                                    "Gagal mengupdate status: " + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("DashboardActivity", "JSON Error: " + e.getMessage());
                            Toast.makeText(DashboardActivityProgressBar.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        Log.e("DashboardActivity", "Volley Error: " + error.getMessage());
                        Toast.makeText(DashboardActivityProgressBar.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(borrowingId));
                params.put("status", status);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}
