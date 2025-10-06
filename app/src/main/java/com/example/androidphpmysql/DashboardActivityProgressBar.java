package com.example.androidphpmysql;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivityProgressBar extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_progress_bar);

        progressBar = findViewById(R.id.progressBar);
        textProgress = findViewById(R.id.textProgress);

        // contoh progress (bisa diganti dinamis nanti)
        int progress = 50;
        progressBar.setProgress(progress);
        textProgress.setText(progress + "%");
    }
}
