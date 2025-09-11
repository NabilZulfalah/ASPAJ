package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class RiwayatActivity : AppCompatActivity() {

    private lateinit var headerWave: ImageView
    private lateinit var footerWave: ImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.riwayat)

        // --- Animasi Header & Footer ---
        headerWave = findViewById(R.id.headerWave)
        footerWave = findViewById(R.id.footerWave)

        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_slide)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_slide)

        headerWave.startAnimation(topAnimation)
        footerWave.startAnimation(bottomAnimation)

        // --- Toolbar + Drawer ---
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- Handle menu di Navigation Drawer ---
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, activity_main_menu::class.java))
                }
                R.id.nav_list_barang -> {
                    startActivity(Intent(this, activity_list_barang::class.java))
                }
                R.id.nav_peminjam -> {
                    startActivity(Intent(this, activity_data_peminjam::class.java))
                }
                R.id.nav_riwayat -> {
                    // Sudah di Riwayat, tidak perlu pindah
                }
                R.id.nav_pengembalian -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Supaya icon menu (hamburger) bisa jalan
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
