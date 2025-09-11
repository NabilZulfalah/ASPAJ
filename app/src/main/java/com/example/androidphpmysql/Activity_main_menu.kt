package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.androidphpmysql.data.activity_data_peminjam
import com.example.androidphpmysql.data.pengembalianActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class Activity_main_menu : AppCompatActivity() {

    private lateinit var headerWave: ImageView
    private lateinit var footerWave: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationView: NavigationView

    // --- untuk animasi typing ---
    private lateinit var typingText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var index = 0
    private val textToType = "Halo,Selamat datang!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // --- Animasi header/footer ---
        headerWave = findViewById(R.id.headerWave)
        footerWave = findViewById(R.id.footerWave)

        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_slide)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_slide)

        headerWave.startAnimation(topAnimation)
        footerWave.startAnimation(bottomAnimation)

        // --- Toolbar + Drawer setup ---
        toolbar = findViewById(R.id.toolbar)
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

        // --- Handle item menu NavigationDrawer ---
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_list_barang -> {
                    startActivity(Intent(this, activity_list_barang::class.java))
                }
                R.id.nav_peminjam -> {
                    startActivity(Intent(this, activity_data_peminjam::class.java))
                }
                R.id.nav_riwayat -> {
                    startActivity(Intent(this, RiwayatActivity::class.java))
                }
                R.id.nav_pengembalian -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // --- Tombol LinearLayout (opsional) ---
        findViewById<LinearLayout>(R.id.listbarangBtn).setOnClickListener {
            startActivity(Intent(this, activity_list_barang::class.java))
        }
        findViewById<LinearLayout>(R.id.riwayatBtn).setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.peminjamBtn).setOnClickListener {
            startActivity(Intent(this, activity_data_peminjam::class.java))
        }
        findViewById<LinearLayout>(R.id.pengembalianBtn).setOnClickListener {
            startActivity(Intent(this, pengembalianActivity::class.java))
        }

        // --- Inisialisasi TextView + mulai animasi typing ---
        typingText = findViewById(R.id.typingText)
        startTypingAnimation()
    }

    private fun startTypingAnimation() {
        index = 0
        typingText.text = ""
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (index < textToType.length) {
                    typingText.append(textToType[index].toString())
                    index++
                    handler.postDelayed(this, 80) // kecepatan ketik
                }
            }
        }, 80)
    }

    // Supaya toggle hamburger ikut handle tombol back/home
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
