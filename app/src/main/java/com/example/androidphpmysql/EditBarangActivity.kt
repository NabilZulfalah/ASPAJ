package com.example.androidphpmysql

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.json.JSONObject

class EditBarangActivity : AppCompatActivity() {

    private var asset: Asset? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_barang)

        // Ambil data barang dari intent
        val assetJson = intent.getStringExtra("ASSET")
        if (!assetJson.isNullOrEmpty()) {
            asset = Gson().fromJson(assetJson, Asset::class.java)
        } else {
            Toast.makeText(this, "Data aset tidak ditemukan!", Toast.LENGTH_SHORT).show()
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
        val jurusanOptions = arrayOf("Teknik Informatika", "Sistem Informasi", "Teknik Elektro", "Teknik Mesin", "Akuntansi", "Manajemen")
        jurusanDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanOptions))

        // Setup dropdown untuk lokasi
        val lokasiOptions = arrayOf("Lab Komputer 1", "Lab Komputer 2", "Lab Elektro", "Gudang", "Ruang Dosen", "Perpustakaan")
        lokasiDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lokasiOptions))

        // Isi form dengan data barang jika ada
        asset?.let { b ->
            editTextNamaBarang.setText(b.namaBarang)
            editTextKodeBarang.setText(b.kodeBarang)
            editTextStok.setText(b.jumlahStok.toString())
            editTextMerk.setText(b.merk)
            editTextSumberDana.setText(b.sumber)
            editTextTahun.setText(b.tahun)
            editTextDeskripsi.setText(b.deskripsi)
            editTextHarga.setText(b.hargaSatuan.toString())
            jurusanDropdown.setText(b.jurusanBarang, false)
            lokasiDropdown.setText(b.lokasiBarang, false)
        }

        // Tombol Update
        buttonUpdate.setOnClickListener {
            if (validateForm()) {
                updateAsset()
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
        val editTextHarga = findViewById<TextInputEditText>(R.id.editTextHarga)

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

        // Validasi stok harus angka
        val stokText = editTextStok.text.toString()
        if (stokText.toIntOrNull() == null) {
            editTextStok.error = "Stok harus berupa angka"
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

        if (editTextHarga.text.isNullOrEmpty()) {
            editTextHarga.error = "Harga harus diisi"
            return false
        }

        // Validasi harga harus angka
        val hargaText = editTextHarga.text.toString()
        if (hargaText.toDoubleOrNull() == null) {
            editTextHarga.error = "Harga harus berupa angka"
            return false
        }

        return true
    }

    private fun updateAsset() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengupdate data aset...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val stringRequest = object : StringRequest(
            Method.POST, Constants.URL_UPDATE_ASSET,
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = asset?.id.toString() ?: ""
                params["namaBarang"] = findViewById<TextInputEditText>(R.id.editTextNamaBarang).text.toString()
                params["kodeBarang"] = findViewById<TextInputEditText>(R.id.editTextKodeBarang).text.toString()
                params["jumlahStok"] = findViewById<TextInputEditText>(R.id.editTextStok).text.toString()
                params["jurusanBarang"] = findViewById<AutoCompleteTextView>(R.id.jurusanDropdown).text.toString()
                params["lokasiBarang"] = findViewById<AutoCompleteTextView>(R.id.lokasiDropdown).text.toString()
                params["merk"] = findViewById<TextInputEditText>(R.id.editTextMerk).text.toString()
                params["sumberDana"] = findViewById<TextInputEditText>(R.id.editTextSumberDana).text.toString()
                params["tahun"] = findViewById<TextInputEditText>(R.id.editTextTahun).text.toString()
                params["deskripsi"] = findViewById<TextInputEditText>(R.id.editTextDeskripsi).text.toString()
                params["hargaSatuan"] = findViewById<TextInputEditText>(R.id.editTextHarga).text.toString()
                return params
            }
        }

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}
