package com.example.androidphpmysql

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.example.androidphpmysql.ExcelUtils
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class ActivityListBarang : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BarangAdapter
    private lateinit var assetList: MutableList<Asset>
    private lateinit var fullAssetList: MutableList<Asset>
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uri = result.data?.data
            handleFilePickerResult(uri)
        }
    }

    companion object {
        private const val TAG = "ActivityListBarang"
        private const val REQUEST_STORAGE_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_barang)

        initializeViews()
        setupRecyclerView()
        loadAssets()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Daftar Aset"
            setDisplayHomeAsUpEnabled(true)
        }

        // ✅ PERBAIKAN: Pastikan ID RecyclerView sesuai dengan layout
        recyclerView = findViewById(R.id.recyclerViewBarang) // atau R.id.recyclerView
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)

        assetList = mutableListOf()
        fullAssetList = mutableListOf()

        // FAB click listener
        val fabTambahBarang = findViewById<FloatingActionButton>(R.id.fabTambahBarang)
        fabTambahBarang.setOnClickListener {
            showAddOptionsDialog()
        }

        // Search functionality
        val searchEditText = findViewById<TextInputEditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Filter click listener
        val searchContainer = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.searchContainer)
        searchContainer.setEndIconOnClickListener {
            showFilterDialog()
        }
    }

    private fun setupRecyclerView() {
        // ✅ PERBAIKAN: Gunakan BarangAdapter yang benar
        adapter = BarangAdapter(assetList,
            onEditClick = { asset ->
                val intent = Intent(this, EditBarangActivity::class.java)
                intent.putExtra("ASSET", Gson().toJson(asset))
                startActivityForResult(intent, 1)
            },
            onDeleteClick = { asset ->
                showDeleteConfirmation(asset)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadAssets() {
        progressDialog.setMessage("Memuat data aset...")
        progressDialog.show()

        val stringRequest = object : StringRequest(
            Method.GET,
            Constants.URL_GET_ASSETS,
            Response.Listener { response ->
                progressDialog.dismiss()
                Log.d(TAG, "Response: $response")

                if (response.isNullOrEmpty() || response.trim().startsWith("<")) {
                    Toast.makeText(this, "Server tidak merespons", Toast.LENGTH_SHORT).show()
                    loadDummyData()
                    return@Listener
                }

                try {
                    parseAssetsResponse(response)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing response", e)
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
                    loadDummyData()
                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Log.e(TAG, "Volley error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                loadDummyData()
            }
        ) {
            override fun getRetryPolicy() = DefaultRetryPolicy(
                30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun parseAssetsResponse(response: String) {
        assetList.clear()
        fullAssetList.clear()

        val trimmedResponse = response.trim()
        val jsonArray = if (trimmedResponse.startsWith("[")) {
            JSONArray(trimmedResponse)
        } else {
            val jsonObject = JSONObject(trimmedResponse)
            if (jsonObject.has("assets")) jsonObject.getJSONArray("assets")
            else if (jsonObject.has("data")) jsonObject.getJSONArray("data")
            else throw JSONException("Unexpected response format")
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val asset = Asset(
                id = obj.optInt("id", obj.optInt("ID", 0)),
                namaBarang = obj.optString("namaBarang", obj.optString("name", "")),
                kodeBarang = obj.optString("kodeBarang", obj.optString("code", "")),
                jumlahStok = obj.optInt("jumlahStok", obj.optInt("stock", 0)),
                lokasiBarang = obj.optString("lokasiBarang", obj.optString("lokasi", "")),
                jurusanBarang = obj.optString("jurusanBarang", obj.optString("jurusan", "")),
                merk = obj.optString("merk", ""),
                hargaSatuan = obj.optDouble("hargaSatuan", obj.optDouble("harga_satuan", 0.0)),
                sumber = obj.optString("sumber", obj.optString("sumberDana", "")),
                tahun = obj.optString("tahun", ""),
                deskripsi = obj.optString("deskripsi", "")
            )
            assetList.add(asset)
        }

        fullAssetList.addAll(assetList)
        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun loadDummyData() {
        assetList.clear()
        fullAssetList.clear()

        // Data dummy untuk testing
        assetList.addAll(getSampleAssets())
        fullAssetList.addAll(assetList)

        adapter.notifyDataSetChanged()
        updateEmptyState()
        Toast.makeText(this, "Menggunakan data dummy", Toast.LENGTH_SHORT).show()
    }

    private fun getSampleAssets(): List<Asset> {
        return listOf(
            Asset(1, "Laptop Dell", "LP001", 5, "Lab Komputer 1", "Teknik Informatika", "Dell", 12000000.0, "BOS", "2023", "Laptop spesifikasi tinggi"),
            Asset(2, "Proyektor Epson", "PJ002", 3, "Ruang Kelas", "Sistem Informasi", "Epson", 5000000.0, "BOPTN", "2022", "Proyektor HD"),
            Asset(3, "Printer Canon", "PR003", 2, "Ruang Guru", "Multimedia", "Canon", 2500000.0, "BOS", "2023", "Printer warna"),
            Asset(4, "Kamera DSLR", "KM004", 1, "Lab Multimedia", "Multimedia", "Canon", 8000000.0, "BOPTN", "2024", "Kamera untuk praktik")
        )
    }



    private fun showFilterDialog() {
        val filterOptions = arrayOf("Semua", "Stok Rendah (< 5)", "Lab Komputer", "Ruang Kelas")

        AlertDialog.Builder(this)
            .setTitle("Filter Aset")
            .setItems(filterOptions) { _, which ->
                applyFilter(which)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun applyFilter(filterType: Int) {
        assetList.clear()

        when (filterType) {
            0 -> assetList.addAll(fullAssetList) // Semua
            1 -> assetList.addAll(fullAssetList.filter { it.jumlahStok < 5 }) // Stok rendah
            2 -> assetList.addAll(fullAssetList.filter {
                it.lokasiBarang?.contains("Lab", true) == true
            }) // Lab
            3 -> assetList.addAll(fullAssetList.filter {
                it.lokasiBarang?.contains("Ruang", true) == true
            }) // Ruang
        }

        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun performSearch(query: String) {
        assetList.clear()
        if (query.isEmpty()) {
            assetList.addAll(fullAssetList)
        } else {
            val filteredList = fullAssetList.filter { asset ->
                asset.namaBarang?.contains(query, true) == true ||
                asset.kodeBarang?.contains(query, true) == true ||
                asset.lokasiBarang?.contains(query, true) == true ||
                asset.jurusanBarang?.contains(query, true) == true ||
                asset.merk?.contains(query, true) == true
            }
            assetList.addAll(filteredList)
        }
        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun showDeleteConfirmation(asset: Asset) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Aset")
            .setMessage("Yakin hapus ${asset.namaBarang}?")
            .setPositiveButton("Hapus") { _, _ -> deleteAsset(asset) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteAsset(asset: Asset) {
        progressDialog.setMessage("Menghapus aset...")
        progressDialog.show()

        val stringRequest = object : StringRequest(
            Method.POST, Constants.URL_DELETE_ASSET,
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        assetList.remove(asset)
                        fullAssetList.remove(asset)
                        adapter.notifyDataSetChanged()
                        updateEmptyState()
                        Toast.makeText(this, "Aset berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
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
                return mapOf("id" to asset.id.toString())
            }
        }

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun importFromExcel() {
        if (checkStoragePermission()) {
            openFilePicker()
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Pilih file Excel"))
    }

    private fun handleFilePickerResult(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val assets = ExcelUtils.importAssetsFromExcel(this, uri)
            if (assets.isEmpty()) {
                Toast.makeText(this, "Tidak ada aset ditemukan di file Excel", Toast.LENGTH_SHORT).show()
                return
            }

            progressDialog.setMessage("Mengimpor ${assets.size} aset...")
            progressDialog.show()

            var successCount = 0
            var errorCount = 0
            val totalAssets = assets.size

            for (asset in assets) {
                addAsset(asset,
                    onSuccess = {
                        successCount++
                        if (successCount + errorCount == totalAssets) {
                            progressDialog.dismiss()
                            loadAssets()
                            Toast.makeText(this, "Import selesai: $successCount berhasil, $errorCount gagal", Toast.LENGTH_LONG).show()
                        }
                    },
                    onError = {
                        errorCount++
                        if (successCount + errorCount == totalAssets) {
                            progressDialog.dismiss()
                            loadAssets()
                            Toast.makeText(this, "Import selesai: $successCount berhasil, $errorCount gagal", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error mengimpor Excel: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        // Update UI ketika tidak ada data
        val emptyTextView = findViewById<TextView>(R.id.emptyTextView)
        emptyTextView?.visibility = if (assetList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddOptionsDialog() {
        val options = arrayOf("Tambah Manual", "Import dari Excel")
        AlertDialog.Builder(this)
            .setTitle("Pilih Cara Tambah Aset")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, TambahAssetActivity::class.java))
                    1 -> importFromExcel()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun addAsset(asset: Asset, onSuccess: () -> Unit, onError: () -> Unit) {
        val stringRequest = object : StringRequest(
            Method.POST, Constants.URL_ADD_ASSET,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        onSuccess()
                    } else {
                        onError()
                    }
                } catch (e: Exception) {
                    onError()
                }
            },
            Response.ErrorListener {
                onError()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "namaBarang" to (asset.namaBarang ?: ""),
                    "kodeBarang" to (asset.kodeBarang ?: ""),
                    "jumlahStok" to asset.jumlahStok.toString(),
                    "lokasiBarang" to (asset.lokasiBarang ?: ""),
                    "jurusanBarang" to (asset.jurusanBarang ?: ""),
                    "merk" to (asset.merk ?: ""),
                    "hargaSatuan" to asset.hargaSatuan.toString(),
                    "sumberDana" to (asset.sumber ?: ""),
                    "tahun" to (asset.tahun ?: ""),
                    "deskripsi" to (asset.deskripsi ?: "")
                )
            }
        }

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                importFromExcel()
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAssets() // Refresh data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loadAssets() // Refresh data setelah edit
        }
    }
}