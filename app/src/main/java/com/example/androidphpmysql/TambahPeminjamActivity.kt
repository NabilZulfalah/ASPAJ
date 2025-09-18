package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class TambahPeminjamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_peminjam)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etKelas = findViewById<EditText>(R.id.etKelas)
        val btnSimpan = findViewById<Button>(R.id.btnTambah)

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString()
            val kelas = etKelas.text.toString()

            if (nama.isEmpty() || kelas.isEmpty()) {
                Snackbar.make(it, "Harap isi semua data", Snackbar.LENGTH_SHORT).show()
            } else {
                // TODO: Simpan ke database / API
                // Setelah simpan sukses, kembali ke DataPeminjamActivity
                val intent = Intent(this, activity_data_peminjam::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
