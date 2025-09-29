package com.example.androidphpmysql

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Peminjaman : AppCompatActivity() {

    private lateinit var spinnerBarang: Spinner
    private lateinit var spinnerKelas: Spinner
    private lateinit var etNamaSiswa: AutoCompleteTextView
    private lateinit var etNama: EditText
    private lateinit var etTanggalPinjam: EditText
    private lateinit var etTanggalKembali: EditText
    private lateinit var etTujuan: EditText
    private lateinit var etJumlah: EditText
    private lateinit var btnSubmit: Button

    // Drawer & Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navigationView: NavigationView
    private lateinit var headerWave: ImageView
    private lateinit var footerWave: ImageView

    // --- untuk animasi typing ---
    private lateinit var typingText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var index = 0

    // Data
    private val siswaData: MutableMap<String, Array<String>> = HashMap()
    private val listSiswaFiltered: MutableList<String> = ArrayList()
    private val listBarang: MutableList<String> = ArrayList()
    private val listKelas: MutableList<String> = ArrayList()

    private lateinit var siswaAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peminjaman)

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

                R.id.nav_home -> {
                    startActivity(Intent(this, activity_main_menu::class.java))
                }
                R.id.nav_list_barang -> {
                    startActivity(Intent(this, ActivityListBarang::class.java))
                }
                R.id.nav_peminjam -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }
                R.id.nav_riwayat -> {
                    startActivity(Intent(this, RiwayatPeminjaman::class.java))
                }
                R.id.nav_pengembalian -> {
                    startActivity(Intent(this, pengembalianActivity::class.java))
                }

                // ✅ Tambahkan Logout

                R.id.nav_home -> startActivity(Intent(this, activity_main_menu::class.java))
                R.id.nav_list_barang -> {
                    startActivity(Intent(this, ActivityListBarang::class.java))
                }
                R.id.nav_peminjam -> startActivity(Intent(this, Peminjaman::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, RiwayatPeminjaman::class.java))
                R.id.nav_pengembalian -> startActivity(Intent(this, pengembalianActivity::class.java))

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
            }
            drawerLayout.closeDrawers()
            true
        }

        // Inisialisasi form
        spinnerBarang = findViewById(R.id.spinnerBarang)
        spinnerKelas = findViewById(R.id.spinnerKelas)
        etNamaSiswa = findViewById(R.id.etNamaSiswa)
        etNama = findViewById(R.id.etNama)
        etTanggalPinjam = findViewById(R.id.etTanggalPinjam)
        etTanggalKembali = findViewById(R.id.etTanggalKembali)
        etTujuan = findViewById(R.id.etTujuan)
        etJumlah = findViewById(R.id.etJumlah)
        btnSubmit = findViewById(R.id.btnSubmit)

        etTanggalPinjam.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

        fetchSiswa()
        fetchBarang()
        fetchKelas()

        spinnerKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedKelas = listKelas[pos]
                listSiswaFiltered.clear()
                for ((nama, data) in siswaData) {
                    if (data[1] == selectedKelas) {
                        listSiswaFiltered.add(nama)
                    }
                }
                siswaAdapter = ArrayAdapter(this@Peminjaman, android.R.layout.simple_dropdown_item_1line, listSiswaFiltered)
                etNamaSiswa.setAdapter(siswaAdapter)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        etNamaSiswa.setOnItemClickListener { parent, _, position, _ ->
            val selectedNama = parent.getItemAtPosition(position).toString()
            val data = siswaData[selectedNama]
            if (data != null) {
                etNama.setText(data[0])
                val idx = listKelas.indexOf(data[1])
                if (idx >= 0) spinnerKelas.setSelection(idx)
            }
        }

        btnSubmit.setOnClickListener { submitForm() }
    }

    private fun fetchSiswa() {
        val url = "http://192.168.4.50/android/v1/get_siswa.php"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                siswaData.clear()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj: JSONObject = jsonArray.getJSONObject(i)
                        val nama = obj.getString("name")
                        val kelas = obj.getString("kelas")
                        siswaData[nama] = arrayOf(nama, kelas)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing siswa data", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error fetching siswa: ${error.message}", Toast.LENGTH_SHORT).show() })
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun fetchBarang() {
        val url = "http://192.168.4.50/android/v1/get_barang.php"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                listBarang.clear()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj: JSONObject = jsonArray.getJSONObject(i)
                        listBarang.add(obj.getString("name"))
                    }
                    spinnerBarang.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listBarang)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing barang data", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error fetching barang: ${error.message}", Toast.LENGTH_SHORT).show() })
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun fetchKelas() {
        val url = "http://192.168.4.50/android/v1/get_kelas.php"
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                listKelas.clear()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        listKelas.add(jsonArray.getString(i))
                    }
                    spinnerKelas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listKelas)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing kelas data", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error fetching kelas: ${error.message}", Toast.LENGTH_SHORT).show() })
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun submitForm() {
        val nama = etNama.text.toString()
        val kelas = spinnerKelas.selectedItem?.toString() ?: ""
        val tglPinjam = etTanggalPinjam.text.toString()
        val tglKembali = etTanggalKembali.text.toString()
        val tujuan = etTujuan.text.toString()
        val barang = spinnerBarang.selectedItem?.toString() ?: ""
        val jumlah = etJumlah.text.toString()

        if (nama.isEmpty() || kelas.isEmpty() || tglPinjam.isEmpty() || tglKembali.isEmpty() || tujuan.isEmpty() || barang.isEmpty() || jumlah.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi sebelum submit", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(Method.POST, "http://192.168.4.50/android/v1/peminjaman.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val message = jsonResponse.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["nama"] = nama
                params["kelas"] = kelas
                params["tgl_pinjam"] = tglPinjam
                params["tgl_kembali"] = tglKembali
                params["tujuan"] = tujuan
                params["barang"] = barang
                params["jumlah"] = jumlah
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
        }
}
