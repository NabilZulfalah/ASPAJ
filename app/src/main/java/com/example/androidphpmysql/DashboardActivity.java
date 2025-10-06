package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.models.Borrowing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private TextView textViewPeminjamAktif, textViewTotalAset, textViewMenungguPersetujuan, textViewBelumDikembalikan;
    private RecyclerView recyclerViewPending;
    private ProgressBar progressBar;

    private PendingBorrowingsAdapter adapter;
    private List<Borrowing> pendingBorrowingsList;
    private ProgressDialog progressDialog;

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

        // Welcome
        String username = SharedPrefManager.getInstance(this).getUsername();
        textViewWelcome.setText("Selamat datang " + username + " di peminjaman aset");

        // RecyclerView setup
        pendingBorrowingsList = new ArrayList<>();
        adapter = new PendingBorrowingsAdapter(this, pendingBorrowingsList);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setAdapter(adapter);

        // Listener approve/reject
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

        // Load data awal
        loadDashboardData();
        loadPendingBorrowings();
    }

    private void loadDashboardData() {
        // sementara static, bisa kamu ambil dari API kalau ada
        textViewPeminjamAktif.setText("0");
        textViewTotalAset.setText("0");
        textViewMenungguPersetujuan.setText("0");
        textViewBelumDikembalikan.setText("0");
    }

    private void loadPendingBorrowings() {
        showProgressDialog("Memuat data...");

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_PENDING_BORROWINGS,
                response -> {
                    hideProgressDialog();
                    Log.d("DashboardActivity", "Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            pendingBorrowingsList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);

                                int id = obj.getInt("id");
                                String studentName = obj.getString("student_name");
                                String borrowDate = obj.optString("borrow_date", "-");
                                String returnDate = obj.optString("return_date", "-");
                                String status = obj.optString("status", "pending");
                                String tujuan = obj.optString("tujuan", "-");
                                String kelas = obj.optString("class", "-");

                                pendingBorrowingsList.add(
                                        new Borrowing(id, studentName, borrowDate, returnDate, status, tujuan, kelas)
                                );
                            }

                            adapter.notifyDataSetChanged();
                            textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                        } else {
                            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    hideProgressDialog();
                    Log.e("DashboardActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateBorrowingStatus(int borrowingId, String status, int position) {
        showProgressDialog("Memproses...");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_STATUS,
                response -> {
                    hideProgressDialog();
                    Log.d("DashboardActivity", "Update Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(this,
                                    status.equals("approved") ? "Peminjaman disetujui" : "Peminjaman ditolak",
                                    Toast.LENGTH_SHORT).show();

                            // Hapus dari list
                            pendingBorrowingsList.remove(position);
                            adapter.notifyItemRemoved(position);

                            // Update counter
                            textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                        } else {
                            Toast.makeText(this,
                                    "Gagal mengupdate: " + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    hideProgressDialog();
                    Log.e("DashboardActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing() && !isDestroyed()) {
            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                Log.e("DashboardActivity", "Error dismissing dialog: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
