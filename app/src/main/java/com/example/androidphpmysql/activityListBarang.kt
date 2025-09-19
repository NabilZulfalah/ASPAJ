package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.util.*

class activityListBarang : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BarangAdapter
    private lateinit var searchEditText: EditText
    private lateinit var filterButton: Button
    private lateinit var fabTambahBarang: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var emptyTextView: TextView

    private val barangList = mutableListOf<Barang>()
    private val filteredList = mutableListOf<Barang>()

    // Launcher untuk edit barang
    private val editBarangLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedBarangJson = result.data?.getStringExtra("UPDATED_BARANG")
            if (updatedBarangJson != null) {
                val updatedBarang = Gson().fromJson(updatedBarangJson, Barang::class.java)
                val index = barangList.indexOfFirst { it.id == updatedBarang.id }
                if (index != -1) {
                    barangList[index] = updatedBarang
                    performSearch()
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_barang)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "List Barang"

        recyclerView = findViewById(R.id.recyclerViewBarang)
        searchEditText = findViewById(R.id.searchEditText)
        filterButton = findViewById(R.id.filterButton)
        fabTambahBarang = findViewById(R.id.fabTambahBarang)
        emptyTextView = findViewById(R.id.emptyTextView)

        loadBarangData()

        adapter = BarangAdapter(filteredList,
            onEditClick = { barang ->
                val intent = Intent(this, EditBarangActivity::class.java)
                intent.putExtra("BARANG", Gson().toJson(barang))
                editBarangLauncher.launch(intent)
            },
            onDeleteClick = { barang ->
                showDeleteConfirmation(barang)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        filterButton.setOnClickListener { showFilterDialog() }

        fabTambahBarang.setOnClickListener {
            Toast.makeText(this, "Tambah barang belum diimplementasi", Toast.LENGTH_SHORT).show()
        }

        filteredList.addAll(barangList)
        updateEmptyState()
        adapter.notifyDataSetChanged()
    }

    private fun loadBarangData() {
        barangList.clear()
        barangList.addAll(
            listOf(
                Barang("1","Laptop ASUS","C001",10,"Lab Komputer","RPL","ASUS","Dana BOS","2022","Laptop untuk pembelajaran siswa",8500000.0),
                Barang("2","Proyektor Epson","C002",5,"Ruang Kelas 1","Multimedia","Epson","Hibah","2021","Proyektor ruang kelas",5000000.0),
                Barang("3","Printer Canon","C003",3,"Guru","Umum","Canon","Dana BOS","2023","Printer untuk administrasi",2500000.0)
            )
        )
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim().lowercase(Locale.getDefault())
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(barangList)
        } else {
            filteredList.addAll(barangList.filter {
                it.nama.lowercase(Locale.getDefault()).contains(query) ||
                        it.kode.lowercase(Locale.getDefault()).contains(query) ||
                        it.lokasi.lowercase(Locale.getDefault()).contains(query) ||
                        it.merk.lowercase(Locale.getDefault()).contains(query)
            })
        }
        updateEmptyState()
        adapter.notifyDataSetChanged()
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Semua", "Lab Komputer", "Ruang Kelas", "Guru", "Stok < 5")
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Barang")
            .setItems(filterOptions) { _, which -> applyFilter(which) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun applyFilter(index: Int) {
        filteredList.clear()
        when(index) {
            0 -> filteredList.addAll(barangList)
            1 -> filteredList.addAll(barangList.filter { it.lokasi=="Lab Komputer" })
            2 -> filteredList.addAll(barangList.filter { it.lokasi.contains("Ruang Kelas") })
            3 -> filteredList.addAll(barangList.filter { it.lokasi=="Guru" })
            4 -> filteredList.addAll(barangList.filter { it.stok < 5 })
        }
        updateEmptyState()
        adapter.notifyDataSetChanged()
    }

    private fun showDeleteConfirmation(barang: Barang) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Barang")
            .setMessage("Apakah Anda yakin ingin menghapus ${barang.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                barangList.remove(barang)
                filteredList.remove(barang)
                updateEmptyState()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "${barang.nama} dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateEmptyState() {
        emptyTextView.visibility = if (filteredList.isEmpty()) TextView.VISIBLE else TextView.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
