package com.example.androidphpmysql

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class pengembalian : AppCompatActivity() {

    private lateinit var etNama: AutoCompleteTextView
    private lateinit var spinnerKelas: Spinner
    private lateinit var spinnerKondisi: Spinner
    private lateinit var imgPreview: ImageView
    private lateinit var btnPickPhoto: Button
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private var imageFile: File? = null

    private val PICK_IMAGE = 101
    private val kelasList = arrayOf("X RPL 1", "XI TITL 1", "X DKV 2", "XII TAV 1", "XI TOI 2", "XII TKJ 3")
    private val kondisiList = arrayOf("baik", "rusak")

    private val TIMEOUT_MS = 30000
    private val MAX_RETRIES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNama = findViewById(R.id.etNama)
        spinnerKelas = findViewById(R.id.spinnerKelas)
        spinnerKondisi = findViewById(R.id.spinnerKondisi)
        imgPreview = findViewById(R.id.imgPreview)
        btnPickPhoto = findViewById(R.id.btnPickPhoto)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = ProgressBar.GONE

        // isi spinner kelas
        val kelasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kelasList)
        spinnerKelas.adapter = kelasAdapter

        // isi spinner kondisi
        val kondisiAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kondisiList)
        spinnerKondisi.adapter = kondisiAdapter

        // Event: kelas dipilih → fetch nama dari server
        spinnerKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedKelas = kelasList[position]
                fetchNamaByKelas(selectedKelas)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPickPhoto.setOnClickListener { openGallery() }

        btnSubmit.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val kelas = spinnerKelas.selectedItem.toString()

            if (nama.isEmpty() || kelas.isEmpty()) {
                toast("Nama dan kelas harus diisi")
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                toast("No internet connection.")
                return@setOnClickListener
            }

            checkBorrowing(nama, kelas)
        }
    }

    // 🔹 Ambil nama dari server sesuai kelas
    private fun fetchNamaByKelas(kelas: String) {
        showLoading("Mengambil daftar nama...")

        val url = "http://10.0.2.2/android/v1/get_peminjaman.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                hideLoading()
                try {
                    val obj = JSONObject(response)
                    if (obj.getString("status") == "success") {
                        val arr: JSONArray = obj.getJSONArray("names")
                        val listNama = ArrayList<String>()
                        for (i in 0 until arr.length()) {
                            listNama.add(arr.getString(i))
                        }
                        val namaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listNama)
                        etNama.setAdapter(namaAdapter)
                        etNama.setText("")
                    } else {
                        toast(obj.getString("message"))
                        etNama.setAdapter(null)
                        etNama.setText("")
                    }
                } catch (e: Exception) {
                    toast("Parsing error: ${e.message}")
                }
            },
            {
                hideLoading()
                toast("Network error saat ambil nama")
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["kelas"] = kelas
                return params
            }
        }

        request.retryPolicy = DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(this).add(request)
    }

    // 🔹 Check borrowing
    private fun checkBorrowing(nama: String, kelas: String) {
        showLoading("Checking borrowing data...")

        val url = "http://10.0.2.2/android/v1/checkBorrowing.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                hideLoading()
                if (response.equals("exists", ignoreCase = true)) {
                    val kondisi = spinnerKondisi.selectedItem.toString()
                    updateStatus(nama, kelas, kondisi)
                } else {
                    toast("Data tidak ditemukan / tidak approved")
                }
            },
            {
                hideLoading()
                toast("Error checkBorrowing")
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("nama" to nama, "kelas" to kelas)
            }
        }

        request.retryPolicy = DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(this).add(request)
    }

    // 🔹 Update status jadi returned
    private fun updateStatus(nama: String, kelas: String, kondisi: String) {
        showLoading("Submitting return data...")

        val url = "http://10.0.2.2/android/v1/pengembalian.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                hideLoading()
                toast("Response: $response")
                clearForm()
            },
            {
                hideLoading()
                toast("Error updateStatus")
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "nama" to nama,
                    "kelas" to kelas,
                    "kondisi" to kondisi,
                    "status" to "returned"
                )
            }
        }

        request.retryPolicy = DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        Volley.newRequestQueue(this).add(request)
    }

    // 🔹 Open gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imgPreview.setImageURI(selectedImageUri)
            imageFile = selectedImageUri?.let { File(getRealPathFromURI(it)) }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    // 🔹 Utils
    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnected == true
        } catch (e: Exception) {
            true
        }
    }

    private fun showLoading(message: String) {
        progressBar.visibility = ProgressBar.VISIBLE
        toast(message)
    }

    private fun hideLoading() {
        progressBar.visibility = ProgressBar.GONE
    }

    private fun clearForm() {
        etNama.setText("")
        spinnerKelas.setSelection(0)
        spinnerKondisi.setSelection(0)
        imgPreview.setImageResource(android.R.color.transparent)
        selectedImageUri = null
        imageFile = null
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
