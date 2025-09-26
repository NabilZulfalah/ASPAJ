package com.example.androidphpmysql

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.util.*

class activityListBarang : AppCompatActivity() {

    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationView: NavigationView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BarangAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchContainer: TextInputLayout
    private lateinit var fabTambahBarang: FloatingActionButton
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

        // --- Setup Drawer + Toolbar ---
        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, activity_main_menu::class.java))
                R.id.nav_list_barang -> startActivity(Intent(this, activityListBarang::class.java))
                R.id.nav_peminjam -> startActivity(Intent(this, pengembalianActivity::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, RiwayatActivity::class.java))
                R.id.nav_pengembalian -> startActivity(Intent(this, pengembalianActivity::class.java))
                R.id.nav_logout -> {
                    val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        recyclerView = findViewById(R.id.recyclerViewBarang)
        searchEditText = findViewById(R.id.searchEditText)
        searchContainer = findViewById(R.id.searchContainer)
        fabTambahBarang = findViewById(R.id.fabTambahBarang)
        emptyTextView = findViewById(R.id.emptyTextView)

        // Warna biru aktif
        val activeColor = getColor(R.color.blue_500)
        searchContainer.setBoxStrokeColor(activeColor)
        searchContainer.defaultHintTextColor = ColorStateList.valueOf(activeColor)

        // Listener filter icon
        searchContainer.setEndIconOnClickListener { showFilterMenu() }

        // Fokus search bar -> tetap biru
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus || searchEditText.text!!.isNotEmpty()) {
                searchContainer.setBoxStrokeColor(activeColor)
                searchContainer.defaultHintTextColor = ColorStateList.valueOf(activeColor)
            } else {
                searchContainer.setBoxStrokeColor(Color.GRAY)
                searchContainer.defaultHintTextColor = ColorStateList.valueOf(Color.GRAY)
            }
        }

        loadBarangData()

        adapter = BarangAdapter(filteredList,
            onEditClick = { barang ->
                val intent = Intent(this, EditBarangActivity::class.java)
                intent.putExtra("BARANG", Gson().toJson(barang))
                editBarangLauncher.launch(intent)
            },
            onDeleteClick = { barang -> showDeleteConfirmation(barang) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        fabTambahBarang.setOnClickListener {
            val intent = Intent(this, TambahAssetActivity::class.java)
            startActivity(intent)
        }

        filteredList.addAll(barangList)
        updateEmptyState()
        adapter.notifyDataSetChanged()
    }

    private fun loadBarangData() {
        barangList.clear()
        barangList.addAll(
            listOf(
                Barang(1,"Laptop ASUS","C001",10,"Lab Komputer","RPL","ASUS","Dana BOS","2022","Laptop untuk pembelajaran siswa",8500000.0),
                Barang(2,"Proyektor Epson","C002",5,"Ruang Kelas 1","Multimedia","Epson","Hibah","2021","Proyektor ruang kelas",5000000.0),
                Barang(3,"Printer Canon","C003",3,"Guru","Umum","Canon","Dana BOS","2023","Printer untuk administrasi",2500000.0)
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

    private fun showFilterMenu() {
        val options = arrayOf("Filter Data", "Import dari Excel")
        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Aksi")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showFilterDialog()
                    1 -> importFromExcel()
                }
            }
            .show()
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Semua", "Lab Komputer", "Ruang Kelas", "Guru", "Stok < 5")
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Barang")
            .setItems(filterOptions) { _, which -> applyFilter(which) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun importFromExcel() {
        Toast.makeText(this, "Fitur import Excel belum diimplementasikan", Toast.LENGTH_SHORT).show()
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
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawers()
            } else {
                drawerLayout.openDrawer(navigationView)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
