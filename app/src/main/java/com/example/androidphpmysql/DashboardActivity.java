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
import com.example.androidphpmysql.adapters.PendingBorrowingsAdapter;
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
    private final List<Borrowing> pendingBorrowingsList = new ArrayList<>();
    private ProgressDialog progressDialog;

    private boolean isLoading = false; // 🚫 mencegah panggilan ganda API

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupRecyclerView();
        setupListeners();

        // Load awal
        loadDashboardData();
        loadPendingBorrowings();
    }

    private void initViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewPeminjamAktif = findViewById(R.id.textViewPeminjamAktif);
        textViewTotalAset = findViewById(R.id.textViewTotalAset);
        textViewMenungguPersetujuan = findViewById(R.id.textViewMenungguPersetujuan);
        textViewBelumDikembalikan = findViewById(R.id.textViewBelumDikembalikan);
        recyclerViewPending = findViewById(R.id.recyclerViewPending);
        progressBar = findViewById(R.id.progressBar);

        String username = SharedPrefManager.getInstance(this).getUsername();
        textViewWelcome.setText("Selamat datang " + username + " di peminjaman aset");
    }

    private void setupRecyclerView() {
        adapter = new PendingBorrowingsAdapter(this, pendingBorrowingsList);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setAdapter(adapter);
    }

    private void setupListeners() {
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
    }

    private void loadDashboardData() {
        // sementara static, bisa ambil dari API nanti
        textViewPeminjamAktif.setText("0");
        textViewTotalAset.setText("0");
        textViewMenungguPersetujuan.setText("0");
        textViewBelumDikembalikan.setText("0");
    }

    private void loadPendingBorrowings() {
        if (isLoading) return; // ⛔ cegah double load
        isLoading = true;
        showProgressDialog("Memuat data...");

        StringRequest request = new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_PENDING_BORROWINGS,
                response -> {
                    hideProgressDialog();
                    isLoading = false;
                    Log.d("DashboardActivity", "Response: " + response);

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optString("status").equals("success")) {
                            JSONArray data = json.getJSONArray("data");
                            pendingBorrowingsList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                int id = obj.optInt("id", 0);
                                String studentName = obj.optString("student_name", "-");
                                String borrowDate = obj.optString("borrow_date", "-");
                                String returnDate = obj.optString("return_date", "-");
                                String status = obj.optString("status", "pending");
                                String tujuan = obj.optString("tujuan", "-");
                                String kelas = obj.optString("class", "-");

                                pendingBorrowingsList.add(new Borrowing(id, studentName, borrowDate, returnDate, status, tujuan, kelas));
                            }

                            adapter.notifyDataSetChanged();
                            textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                        } else {
                            Toast.makeText(this, "Tidak ada data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "JSON Error: " + e.getMessage());
                        Toast.makeText(this, "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    hideProgressDialog();
                    isLoading = false;
                    Log.e("DashboardActivity", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
                }
        );

        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private void updateBorrowingStatus(int borrowingId, String status, int position) {
        showProgressDialog("Memproses...");

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.URL_UPDATE_STATUS,
                response -> {
                    hideProgressDialog();
                    Log.d("DashboardActivity", "Update Response: " + response);

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optString("status").equals("success")) {
                            Toast.makeText(this,
                                    status.equals("approved") ? "Peminjaman disetujui" : "Peminjaman ditolak",
                                    Toast.LENGTH_SHORT).show();

                            pendingBorrowingsList.remove(position);
                            adapter.notifyItemRemoved(position);
                            textViewMenungguPersetujuan.setText(String.valueOf(pendingBorrowingsList.size()));
                        } else {
                            Toast.makeText(this, json.optString("message", "Gagal mengupdate"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Kesalahan membaca response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Koneksi gagal: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        if (!isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ⚡ hanya reload data kalau belum ada
        if (pendingBorrowingsList.isEmpty()) {
            loadPendingBorrowings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }
}
