package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import android.widget.TextView

class activity_data_peminjam : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var layoutPeminjam: LinearLayout
    private lateinit var btnTambah: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datapeminjam)

        // --- Init View ---
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        layoutPeminjam = findViewById(R.id.containerPeminjam)
        btnTambah = findViewById(R.id.btnTambah)

        // --- Drawer Toggle ---
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- Navigation Drawer ---
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
<<<<<<< HEAD
                R.id.nav_home -> {
                    startActivity(Intent(this, activity_main_menu::class.java))
                }
                R.id.nav_list_barang -> {
                    startActivity(Intent(this, activityListBarang::class.java))
                }
                R.id.nav_peminjam -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }
                R.id.nav_riwayat -> {
                    startActivity(Intent(this, RiwayatActivity::class.java))
                }
                R.id.nav_pengembalian -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }

                // ✅ Tambahkan Logout
                R.id.nav_logout -> {
                    // Hapus session (kalau pakai SharedPreferences)
                    val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    // Kembali ke LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
=======
                R.id.nav_list_barang -> startActivity(Intent(this, activityListBarang::class.java))
                R.id.nav_peminjam -> { /* sudah di sini */ }
                R.id.nav_riwayat -> startActivity(Intent(this, RiwayatPeminjaman::class.java))
                R.id.nav_pengembalian -> startActivity(Intent(this, pengembalianActivity::class.java))
>>>>>>> origin/Frontend
            }
            drawerLayout.closeDrawers()
            true
        }

        // --- Tombol Tambah ---
        btnTambah.setOnClickListener {
            val intent = Intent(this, TambahPeminjamActivity::class.java)
            startActivityForResult(intent, 100) // supaya bisa menerima data balik
        }
    }

    // --- Terima data balik dari TambahPeminjamActivity ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val nama = data.getStringExtra("NAMA")
            val kelas = data.getStringExtra("KELAS")
            if (!nama.isNullOrEmpty() && !kelas.isNullOrEmpty()) {
                tambahPeminjamKeLayout(nama, kelas)
            }
        }
    }

    // --- Tambahkan data ke layout dalam bentuk CardView ---
    private fun tambahPeminjamKeLayout(nama: String, kelas: String) {
        val card = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            radius = 16f
            cardElevation = 8f
            setContentPadding(24, 24, 24, 24)
        }

        val textView = TextView(this).apply {
            text = "Nama: $nama\nKelas: $kelas"
            textSize = 16f
        }

        card.addView(textView)
        layoutPeminjam.addView(card)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
