package com.example.androidphpmysql;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.Toast;

public class Peminjaman extends AppCompatActivity {

    Spinner spinnerBarang, spinnerKelas;
    AutoCompleteTextView etNamaSiswa;
    EditText etNama, etTanggalPinjam, etTanggalKembali, etTujuan, etJumlah;
    Button btnSubmit;

    // data
    Map<String, String[]> siswaData = new HashMap<>(); // key: nama, value: [nama, kelas]
    List<String> listSiswaFiltered = new ArrayList<>();
    List<String> listBarang = new ArrayList<>();
    List<String> listKelas = new ArrayList<>();

    ArrayAdapter<String> siswaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_peminjaman);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // inisialisasi view
        spinnerBarang = findViewById(R.id.spinnerBarang);
        spinnerKelas = findViewById(R.id.spinnerKelas);
        etNamaSiswa = findViewById(R.id.etNamaSiswa);
        etNama = findViewById(R.id.etNama);
        etTanggalPinjam = findViewById(R.id.etTanggalPinjam);
        etTanggalKembali = findViewById(R.id.etTanggalKembali);
        etTujuan = findViewById(R.id.etTujuan);
        etJumlah = findViewById(R.id.etJumlah);
        btnSubmit = findViewById(R.id.btnSubmit);

        // set tanggal pinjam otomatis
        etTanggalPinjam.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        // fetch data dari API
        fetchSiswa();
        fetchBarang();
        fetchKelas();

        // filter siswa sesuai kelas
        spinnerKelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedKelas = listKelas.get(pos);
                listSiswaFiltered.clear();
                for (Map.Entry<String, String[]> entry : siswaData.entrySet()) {
                    if (entry.getValue()[1].equals(selectedKelas)) {
                        listSiswaFiltered.add(entry.getKey());
                    }
                }
                siswaAdapter = new ArrayAdapter<>(Peminjaman.this, android.R.layout.simple_dropdown_item_1line, listSiswaFiltered);
                etNamaSiswa.setAdapter(siswaAdapter);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // klik suggestion nama siswa
        etNamaSiswa.setOnItemClickListener((parent, view, position, id) -> {
            String selectedNama = parent.getItemAtPosition(position).toString();
            String[] data = siswaData.get(selectedNama);
            if (data != null) {
                etNama.setText(data[0]); // nama lengkap
                int idx = listKelas.indexOf(data[1]);
                if (idx >= 0) spinnerKelas.setSelection(idx);
            }
        });

        // submit
        btnSubmit.setOnClickListener(v -> submitForm());
    }

    // ambil data siswa dari API
    private void fetchSiswa() {
        String url = "http://192.168.4.123/ASPAJ/v1/get_siswa.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    siswaData.clear();
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            org.json.JSONObject obj = jsonArray.getJSONObject(i);
                            String nama = obj.getString("name");
                            String kelas = obj.getString("kelas");
                            siswaData.put(nama, new String[]{nama, kelas});
                        }
                    } catch (Exception e) {
                        Toast.makeText(Peminjaman.this, "Error parsing siswa data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Peminjaman.this, "Error fetching siswa: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // ambil barang
    private void fetchBarang() {
        String url = "http://192.168.4.123/ASPAJ/v1/get_barang.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    listBarang.clear();
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            org.json.JSONObject obj = jsonArray.getJSONObject(i);
                            listBarang.add(obj.getString("name"));
                        }
                        spinnerBarang.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listBarang));
                    } catch (Exception e) {
                        Toast.makeText(Peminjaman.this, "Error parsing barang data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Peminjaman.this, "Error fetching barang: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // ambil kelas
    private void fetchKelas() {
        String url = "http://192.168.4.123/ASPAJ/v1/get_kelas.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    listKelas.clear();
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            listKelas.add(jsonArray.getString(i));
                        }
                        spinnerKelas.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listKelas));
                    } catch (Exception e) {
                        Toast.makeText(Peminjaman.this, "Error parsing kelas data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Peminjaman.this, "Error fetching kelas: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // submit form ke API
    private void submitForm() {
        String nama = etNama.getText().toString();
        String kelas = spinnerKelas.getSelectedItem() != null ? spinnerKelas.getSelectedItem().toString() : "";
        String tglPinjam = etTanggalPinjam.getText().toString();
        String tglKembali = etTanggalKembali.getText().toString();
        String tujuan = etTujuan.getText().toString();
        String barang = spinnerBarang.getSelectedItem() != null ? spinnerBarang.getSelectedItem().toString() : "";
        String jumlah = etJumlah.getText().toString();

        if (nama.isEmpty() || kelas.isEmpty() || tglPinjam.isEmpty() || tglKembali.isEmpty() || tujuan.isEmpty() || barang.isEmpty() || jumlah.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi sebelum submit", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://172.16.100.120/ASPAJ/v1/peminjaman.php",
                response -> {
                    try {
                        org.json.JSONObject jsonResponse = new org.json.JSONObject(response);
                        String message = jsonResponse.getString("message");
                        Toast.makeText(Peminjaman.this, message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(Peminjaman.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Peminjaman.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("kelas", kelas);
                params.put("tgl_pinjam", tglPinjam);
                params.put("tgl_kembali", tglKembali);
                params.put("tujuan", tujuan);
                params.put("barang", barang);
                params.put("jumlah", jumlah);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

}
