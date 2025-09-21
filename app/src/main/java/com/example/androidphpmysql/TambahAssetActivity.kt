package com.example.androidphpmysql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText

class TambahAssetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_asset)

        // Setup dropdown untuk jurusan
        val jurusanDropdown = findViewById<AutoCompleteTextView>(R.id.jurusanDropdown)
        val jurusanOptions = arrayOf("RPL", "Multimedia", "TKJ", "TKR", "TSM", "Tav", "TITL", "TGB")
        val adapterJurusan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanOptions)
        jurusanDropdown.setAdapter(adapterJurusan)

        // Setup dropdown untuk lokasi
        val lokasiDropdown = findViewById<AutoCompleteTextView>(R.id.lokasiDropdown)
        val lokasiOptions = arrayOf("Lab Komputer", "Ruang Kelas 1", "Ruang Kelas 2", "Ruang Guru", "Perpustakaan")
        val adapterLokasi = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lokasiOptions)
        lokasiDropdown.setAdapter(adapterLokasi)

        val buttonSimpan = findViewById<Button>(R.id.buttonSimpan)
        buttonSimpan.setOnClickListener {
            // Logika penyimpanan data
            val intent = Intent(this, activityListBarang::class.java)
            startActivity(intent)
            finish()
        }

        val buttonBatal = findViewById<Button>(R.id.buttonBatal)
        buttonBatal.setOnClickListener {
            finish()
        }
    }
}