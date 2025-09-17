package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class activity_list_barang : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_barang)

        // --- Toolbar + Drawer ---
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- Handle menu Navigation Drawer ---
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, activity_main_menu::class.java))
                R.id.nav_list_barang -> {} // sudah di sini
                R.id.nav_peminjam -> startActivity(Intent(this, activity_data_peminjam::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, RiwayatActivity::class.java))
                R.id.nav_pengembalian -> startActivity(Intent(this, pengembalianActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        // --- Isi daftar asset dinamis ---
        val container = findViewById<LinearLayout>(R.id.qqassetMenuContainer)
        val jurusan = listOf(
            "Asset RPL" to R.drawable.ic_laptop,
            "Asset DKV" to R.drawable.ic_paintbrush,
            "Asset TITL" to R.drawable.ic_bolt,
            "Asset TAV" to R.drawable.ic_speaker,
            "Asset TKJ" to R.drawable.ic_chip,
        )

        for ((label, icon) in jurusan) {
            val view = layoutInflater.inflate(R.layout.activity_item_asset_menu, container, false)
            val iconView = view.findViewById<ImageView>(R.id.assetIcon)
            val labelView = view.findViewById<TextView>(R.id.assetLabel)

            iconView.setImageResource(icon)
            labelView.text = label

            view.setOnClickListener {
                Toast.makeText(this, "Kamu memilih: $label", Toast.LENGTH_SHORT).show()
            }

            container.addView(view)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }
}
