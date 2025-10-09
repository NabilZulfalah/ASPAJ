package com.example.androidphpmysql;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.adapters.PendingBorrowingsAdapter;
import com.example.androidphpmysql.models.Borrowing;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements PendingBorrowingsAdapter.OnApproveRejectListener {

    private TextView textViewWelcome;
    private TextView textViewPeminjamAktif, textViewTotalAset, textViewMenungguPersetujuan, textViewBelumDikembalikan;
    private RecyclerView recyclerViewPending;

    private SQLiteDatabase database;

    private PendingBorrowingsAdapter adapter;
    private List<Borrowing> pendingBorrowingsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupRecyclerView();
        setupDatabase();
        loadData();
    }

    private void initializeViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewPeminjamAktif = findViewById(R.id.textViewPeminjamAktif);
        textViewTotalAset = findViewById(R.id.textViewTotalAset);
        textViewMenungguPersetujuan = findViewById(R.id.textViewMenungguPersetujuan);
        textViewBelumDikembalikan = findViewById(R.id.textViewBelumDikembalikan);
        recyclerViewPending = findViewById(R.id.recyclerViewPending);

        // Set welcome message with username
        String username = SharedPrefManager.getInstance(this).getUsername();
        textViewWelcome.setText("Selamat datang user " + username + " di peminjaman aset");
    }

    private void setupRecyclerView() {
        pendingBorrowingsList = new ArrayList<>();
        adapter = new PendingBorrowingsAdapter(this, pendingBorrowingsList);
        adapter.setOnApproveRejectListener(this);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setAdapter(adapter);
    }

    private void setupDatabase() {
        try {
            database = openOrCreateDatabase("asetkejuruan.db", MODE_PRIVATE, null);
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error opening database", e);
            Toast.makeText(this, "Error membuka database", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        if (database == null) {
            Log.e("DashboardActivity", "Database is null");
            return;
        }

        // Check if tables exist
        if (checkTableExists("borrowings") && checkTableExists("students") && checkTableExists("commodities")) {
            loadDashboardData();
            loadPendingBorrowings();
        } else {
            Log.e("DashboardActivity", "Salah satu tabel tidak ditemukan di database!");
            Toast.makeText(this, "Tabel database tidak lengkap", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkTableExists(String tableName) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{tableName});
            exists = cursor.moveToFirst();
        } catch (SQLException e) {
            Log.e("DashboardActivity", "Error checking table: " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return exists;
    }

    private void loadDashboardData() {
        if (database == null) return;

        Cursor cursor;

        // Peminjam Aktif
        int peminjamAktif = 0;
        cursor = database.rawQuery(
                "SELECT COUNT(DISTINCT student_id) FROM borrowings WHERE status = 'approved' AND (return_date IS NULL OR return_date > date('now'))",
                null);
        if (cursor.moveToFirst()) {
            peminjamAktif = cursor.getInt(0);
        }
        cursor.close();
        textViewPeminjamAktif.setText(String.valueOf(peminjamAktif));

        // Total Aset
        int totalAset = 0;
        cursor = database.rawQuery("SELECT SUM(stock) FROM commodities", null);
        if (cursor.moveToFirst()) {
            totalAset = cursor.getInt(0);
        }
        cursor.close();
        textViewTotalAset.setText(String.valueOf(totalAset));

        // Menunggu Persetujuan
        int menungguPersetujuan = 0;
        cursor = database.rawQuery("SELECT COUNT(*) FROM borrowings WHERE status = 'pending'", null);
        if (cursor.moveToFirst()) {
            menungguPersetujuan = cursor.getInt(0);
        }
        cursor.close();
        textViewMenungguPersetujuan.setText(String.valueOf(menungguPersetujuan));

        // Belum Dikembalikan
        int belumDikembalikan = 0;
        cursor = database.rawQuery(
                "SELECT COUNT(*) FROM borrowings WHERE status = 'approved' AND return_date < date('now')",
                null);
        if (cursor.moveToFirst()) {
            belumDikembalikan = cursor.getInt(0);
        }
        cursor.close();
        textViewBelumDikembalikan.setText(String.valueOf(belumDikembalikan));
    }

    private void loadPendingBorrowings() {
        if (database == null) return;

        pendingBorrowingsList.clear();

        Cursor cursor = database.rawQuery(
                "SELECT b.id, s.name, b.borrow_date, b.return_date, b.tujuan, s.kelas, b.status FROM borrowings b " +
                        "JOIN students s ON b.student_id = s.id WHERE b.status = 'pending'",
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String studentName = cursor.getString(1);
                    String borrowDate = cursor.getString(2);
                    String returnDate = cursor.isNull(3) ? "-" : cursor.getString(3);
                    String tujuan = cursor.getString(4);
                    String kelas = cursor.getString(5);
                    String status = cursor.getString(6);

                    // Create Borrowing object with all required fields
                    Borrowing borrowing = new Borrowing(id, studentName, tujuan, kelas, borrowDate, returnDate, status);
                    pendingBorrowingsList.add(borrowing);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onApproveClick(Borrowing borrowing, int position) {
        // Implement approve logic here
        updateBorrowingStatus(borrowing.getId(), "approved", position);
        Toast.makeText(this, "Menyetujui peminjaman: " + borrowing.getStudentName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRejectClick(Borrowing borrowing, int position) {
        // Implement reject logic here
        updateBorrowingStatus(borrowing.getId(), "rejected", position);
        Toast.makeText(this, "Menolak peminjaman: " + borrowing.getStudentName(), Toast.LENGTH_SHORT).show();
    }

    private void updateBorrowingStatus(int borrowingId, String newStatus, int position) {
        if (database == null) return;

        try {
            database.execSQL(
                    "UPDATE borrowings SET status = ? WHERE id = ?",
                    new String[]{newStatus, String.valueOf(borrowingId)}
            );

            // Remove from list
            adapter.removeItem(position);

            // Refresh dashboard data
            loadDashboardData();

        } catch (SQLException e) {
            Log.e("DashboardActivity", "Error updating borrowing status", e);
            Toast.makeText(this, "Error mengupdate status peminjaman", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}