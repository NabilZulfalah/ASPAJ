package com.example.androidphpmysql

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class EditBarangActivity : AppCompatActivity() {

    private var barang: Barang? = null  // gunakan nullable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_barang)

        // Ambil data barang dari intent
        val barangJson = intent.getStringExtra("BARANG")
        if (!barangJson.isNullOrEmpty()) {
            barang = Gson().fromJson(barangJson, Barang::class.java)
        } else {
            Toast.makeText(this, "Data barang tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inisialisasi view
        val editTextNamaBarang = findViewById<TextInputEditText>(R.id.editTextNamaBarang)
        val editTextKodeBarang = findViewById<TextInputEditText>(R.id.editTextKodeBarang)
        val editTextStok = findViewById<TextInputEditText>(R.id.editTextStok)
        val jurusanDropdown = findViewById<AutoCompleteTextView>(R.id.jurusanDropdown)
        val lokasiDropdown = findViewById<AutoCompleteTextView>(R.id.lokasiDropdown)
        val editTextMerk = findViewById<TextInputEditText>(R.id.editTextMerk)
        val editTextSumberDana = findViewById<TextInputEditText>(R.id.editTextSumberDana)
        val editTextTahun = findViewById<TextInputEditText>(R.id.editTextTahun)
        val editTextDeskripsi = findViewById<TextInputEditText>(R.id.editTextDeskripsi)
        val editTextHarga = findViewById<TextInputEditText>(R.id.editTextHarga)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        val buttonBatal = findViewById<Button>(R.id.buttonBatal)

        // Setup dropdown untuk jurusan
        val jurusanOptions = arrayOf("RPL", "Multimedia", "TKJ", "TKR", "TSM", "Tav", "TITL", "TGB")
        jurusanDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanOptions))

        // Setup dropdown untuk lokasi
        val lokasiOptions = arrayOf("Lab Komputer", "Ruang Kelas 1", "Ruang Kelas 2", "Ruang Guru", "Perpustakaan")
        lokasiDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lokasiOptions))

        // Isi form dengan data barang jika ada
        barang?.let { b ->
            editTextNamaBarang.setText(b.nama)
            editTextKodeBarang.setText(b.kode)
            editTextStok.setText(b.stok.toString())
            editTextMerk.setText(b.merk)
            editTextSumberDana.setText(b.sumberDana)
            editTextTahun.setText(b.tahun)
            editTextDeskripsi.setText(b.deskripsi)
            editTextHarga.setText(b.harga.toString())
            jurusanDropdown.setText(b.jurusan, false)
            lokasiDropdown.setText(b.lokasi, false)
        }

        // Tombol Update
        buttonUpdate.setOnClickListener {
            if (validateForm()) {
                val updatedBarang = barang?.copy(
                    nama = editTextNamaBarang.text.toString(),
                    kode = editTextKodeBarang.text.toString(),
                    stok = editTextStok.text.toString().toInt(),
                    jurusan = jurusanDropdown.text.toString(),
                    lokasi = lokasiDropdown.text.toString(),
                    merk = editTextMerk.text.toString(),
                    sumberDana = editTextSumberDana.text.toString(),
                    tahun = editTextTahun.text.toString(),
                    deskripsi = editTextDeskripsi.text.toString(),
                    harga = editTextHarga.text.toString().toDouble()
                )

                updatedBarang?.let { ub ->
                    val resultIntent = Intent()
                    resultIntent.putExtra("UPDATED_BARANG", Gson().toJson(ub))
                    setResult(Activity.RESULT_OK, resultIntent)
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        // Tombol Batal
        buttonBatal.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val editTextNamaBarang = findViewById<TextInputEditText>(R.id.editTextNamaBarang)
        val editTextKodeBarang = findViewById<TextInputEditText>(R.id.editTextKodeBarang)
        val editTextStok = findViewById<TextInputEditText>(R.id.editTextStok)
        val jurusanDropdown = findViewById<AutoCompleteTextView>(R.id.jurusanDropdown)
        val lokasiDropdown = findViewById<AutoCompleteTextView>(R.id.lokasiDropdown)

        if (editTextNamaBarang.text.isNullOrEmpty()) {
            editTextNamaBarang.error = "Nama barang harus diisi"
            return false
        }

        if (editTextKodeBarang.text.isNullOrEmpty()) {
            editTextKodeBarang.error = "Kode barang harus diisi"
            return false
        }

        if (editTextStok.text.isNullOrEmpty()) {
            editTextStok.error = "Stok harus diisi"
            return false
        }

        if (jurusanDropdown.text.isNullOrEmpty()) {
            jurusanDropdown.error = "Jurusan harus dipilih"
            return false
        }

        if (lokasiDropdown.text.isNullOrEmpty()) {
            lokasiDropdown.error = "Lokasi harus dipilih"
            return false
        }

        return true
    }
}
