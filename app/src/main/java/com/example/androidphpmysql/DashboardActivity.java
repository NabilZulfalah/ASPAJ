package com.example.androidphpmysql;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.adapters.PendingBorrowingsAdapter;
import com.example.androidphpmysql.models.Borrowing;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

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

        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewPeminjamAktif = findViewById(R.id.textViewPeminjamAktif);
        textViewTotalAset = findViewById(R.id.textViewTotalAset);
        textViewMenungguPersetujuan = findViewById(R.id.textViewMenungguPersetujuan);
        textViewBelumDikembalikan = findViewById(R.id.textViewBelumDikembalikan);
        recyclerViewPending = findViewById(R.id.recyclerViewPending);

        // Set welcome message with username
        String username = SharedPrefManager.getInstance(this).getUsername();
        textViewWelcome.setText("Selamat datang user " + username + " di peminjaman aset");

        // Open or create database
        database = openOrCreateDatabase("asetkejuruan.db", MODE_PRIVATE, null);

        // Cek apakah tabel ada
        if (checkTableExists("borrowings") && checkTableExists("students") && checkTableExists("commodities")) {
            loadDashboardData();
            loadPendingBorrowings();
        } else {
            Log.e("DashboardActivity", "Salah satu tabel tidak ditemukan di database!");
        }

        // Setup RecyclerView dengan list kosong dulu
        pendingBorrowingsList = new ArrayList<>();
        adapter = new PendingBorrowingsAdapter(pendingBorrowingsList);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPending.setAdapter(adapter);
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
        pendingBorrowingsList.clear();

        Cursor cursor = database.rawQuery(
                "SELECT b.id, s.name, b.borrow_date, b.return_date FROM borrowings b " +
                        "JOIN students s ON b.student_id = s.id WHERE b.status = 'pending'",
                null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String studentName = cursor.getString(1);
                String borrowDate = cursor.getString(2);
                String returnDate = cursor.isNull(3) ? "-" : cursor.getString(3); // handle null date

                Borrowing borrowing = new Borrowing(id, studentName, borrowDate, returnDate);
                pendingBorrowingsList.add(borrowing);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged(); // refresh RecyclerView
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
