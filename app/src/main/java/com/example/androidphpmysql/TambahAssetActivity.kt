package com.example.androidphpmysql

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.androidphpmysql.RequestHandler
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class TambahAssetActivity : AppCompatActivity() {

    // ✅ PERBAIKAN: Gunakan AutoCompleteTextView bukan Spinner
    private lateinit var dropdownJurusan: MaterialAutoCompleteTextView
    private lateinit var dropdownLokasi: MaterialAutoCompleteTextView
    private lateinit var editTextNama: TextInputEditText
    private lateinit var editTextKode: TextInputEditText
    private lateinit var editTextStok: TextInputEditText
    private lateinit var editTextMerk: TextInputEditText
    private lateinit var editTextSumber: TextInputEditText
    private lateinit var editTextTahun: TextInputEditText
    private lateinit var editTextDeskripsi: TextInputEditText
    private lateinit var editTextHarga: TextInputEditText
    private lateinit var buttonSimpan: Button
    private lateinit var buttonBatal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_asset)

        initializeViews()
        setupDropdowns()
        setupButtonListener()
    }

    private fun initializeViews() {
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Tambah Asset"
            setDisplayHomeAsUpEnabled(true)
        }

        // ✅ PERBAIKAN: Gunakan findViewById dengan type yang benar
        dropdownJurusan = findViewById(R.id.jurusanDropdown)
        dropdownLokasi = findViewById(R.id.lokasiDropdown)
        editTextNama = findViewById(R.id.editTextNamaBarang)
        editTextKode = findViewById(R.id.editTextKodeBarang)
        editTextStok = findViewById(R.id.editTextStok)
        editTextMerk = findViewById(R.id.editTextMerk)
        editTextSumber = findViewById(R.id.editTextSumberDana)
        editTextTahun = findViewById(R.id.editTextTahun)
        editTextDeskripsi = findViewById(R.id.editTextDeskripsi)
        editTextHarga = findViewById(R.id.editTextHarga)
        buttonSimpan = findViewById(R.id.buttonSimpan)
        buttonBatal = findViewById(R.id.buttonBatal)

    }

    private fun setupDropdowns() {
        // Data untuk dropdown jurusan
        val jurusanList = arrayOf(
            "Teknik Informatika", "Sistem Informasi", "Teknik Elektro",
            "Teknik Mesin", "Akuntansi", "Manajemen"
        )

        // Data untuk dropdown lokasi
        val lokasiList = arrayOf(
            "Lab Komputer 1", "Lab Komputer 2", "Lab Elektro",
            "Gudang", "Ruang Dosen", "Perpustakaan"
        )

        // ✅ Setup adapter untuk AutoCompleteTextView
        val jurusanAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanList)
        val lokasiAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lokasiList)

        dropdownJurusan.setAdapter(jurusanAdapter)
        dropdownLokasi.setAdapter(lokasiAdapter)
    }

    private fun setupButtonListener() {
        buttonSimpan.setOnClickListener {
            tambahAsset()
        }
        buttonBatal.setOnClickListener {
            finish()
        }
    }

    private fun tambahAsset() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Menambah asset...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val nama = editTextNama.text.toString()
        val kode = editTextKode.text.toString()
        val stok = editTextStok.text.toString().toIntOrNull() ?: 0
        val jurusan = dropdownJurusan.text.toString()
        val lokasi = dropdownLokasi.text.toString()
        val merk = editTextMerk.text.toString()
        val sumber = editTextSumber.text.toString()
        val tahun = editTextTahun.text.toString()
        val deskripsi = editTextDeskripsi.text.toString()
        val harga = editTextHarga.text.toString().toDoubleOrNull() ?: 0.0

        if (nama.isEmpty() || kode.isEmpty()) {
            progressDialog.dismiss()
            Toast.makeText(this, "Nama dan Kode barang harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(
            Method.POST, Constants.URL_ADD_ASSET,
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(this, "Asset berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal menambah asset: ${jsonObject.getString("message")}", Toast.LENGTH_SHORT).show()
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
                params["namaBarang"] = nama
                params["kodeBarang"] = kode
                params["jumlahStok"] = stok.toString()
                params["jurusanBarang"] = jurusan
                params["lokasiBarang"] = lokasi
                params["merk"] = merk
                params["sumberDana"] = sumber
                params["tahun"] = tahun
                params["deskripsi"] = deskripsi
                params["hargaSatuan"] = harga.toString()
                return params
            }
        }

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}
