package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectJurusanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_jurusan);

        // Lihat Semua Barang
        Button btnSemua = findViewById(R.id.btnSemua);
        btnSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("all");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "all");
                startActivity(intent);
                finish();
            }
        });

        // Rekayasa Perangkat Lunak
        Button btnRekayasaPerangkatLunak = findViewById(R.id.btnRekayasaPerangkatLunak);
        btnRekayasaPerangkatLunak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Rekayasa Perangkat Lunak");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Rekayasa Perangkat Lunak");
                startActivity(intent);
                finish();
            }
        });

        // Desain Komunikasi Visual
        Button btnDesainKomunikasiVisual = findViewById(R.id.btnDesainKomunikasiVisual);
        btnDesainKomunikasiVisual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Desain Komunikasi Visual");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Desain Komunikasi Visual");
                startActivity(intent);
                finish();
            }
        });

        // Teknik Otomasi Industri
        Button btnTeknikOtomasiIndustri = findViewById(R.id.btnTeknikOtomasiIndustri);
        btnTeknikOtomasiIndustri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Teknik Otomasi Industri");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Teknik Otomasi Industri");
                startActivity(intent);
                finish();
            }
        });

        // Teknik Instalasi Tenaga Listrik
        Button btnTeknikInstalasiTenagaListrik = findViewById(R.id.btnTeknikInstalasiTenagaListrik);
        btnTeknikInstalasiTenagaListrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Teknik Instalasi Tenaga Listrik");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Teknik Instalasi Tenaga Listrik");
                startActivity(intent);
                finish();
            }
        });

        // Teknik Audio Video
        Button btnTeknikAudioVideo = findViewById(R.id.btnTeknikAudioVideo);
        btnTeknikAudioVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Teknik Audio Video");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Teknik Audio Video");
                startActivity(intent);
                finish();
            }
        });

        // Teknik Komputer Jaringan
        Button btnTeknikKomputerJaringan = findViewById(R.id.btnTeknikKomputerJaringan);
        btnTeknikKomputerJaringan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SelectJurusanActivity.this).saveJurusan("Teknik Komputer Jaringan");
                Intent intent = new Intent(SelectJurusanActivity.this, AssetListActivity.class);
                intent.putExtra("jurusan", "Teknik Komputer Jaringan");
                startActivity(intent);
                finish();
            }
        });
    }
}
