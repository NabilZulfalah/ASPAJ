package com.example.androidphpmysql

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class TambahPeminjamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_peminjam)

        val edtNama = findViewById<EditText>(R.id.edtNama)
        val edtKelas = findViewById<EditText>(R.id.edtKelas)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val nama = edtNama.text.toString()
            val kelas = edtKelas.text.toString()

            // Kirim balik ke activity_data_peminjam
            val resultIntent = Intent()
            resultIntent.putExtra("NAMA", nama)
            resultIntent.putExtra("KELAS", kelas)
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // kembali ke halaman sebelumnya
        }
    }
}
