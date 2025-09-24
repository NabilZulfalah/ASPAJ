//package com.example.androidphpmysql;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class pengembalian extends AppCompatActivity {
//
//    AutoCompleteTextView etNama;
//    Spinner spinnerKelas, spinnerKondisi;
//    ImageView imgPreview;
//    Button btnPickPhoto, btnSubmit;
//    ProgressBar progressBar;
//
//    Uri selectedImageUri;
//    File imageFile;
//    private static final int PICK_IMAGE = 101;
//
//    String[] kelasList = {"X RPL 1", "XI TITL 1", "X DKV 2", "XII TAV 1", "XI TOI 2", "XII TKJ 3"};
//    String[] kondisiList = {"baik", "rusak"};
//
//    private static final int TIMEOUT_MS = 30000; // 30 seconds
//    private static final int MAX_RETRIES = 3;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_return_form);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        etNama = findViewById(R.id.etNama);
//        spinnerKelas = findViewById(R.id.spinnerKelas);
//        spinnerKondisi = findViewById(R.id.spinnerKondisi);
//        imgPreview = findViewById(R.id.imgPreview);
//        btnPickPhoto = findViewById(R.id.btnPickPhoto);
//        btnSubmit = findViewById(R.id.btnSubmit);
//        progressBar = findViewById(R.id.progressBar);
//
//        if (progressBar != null) {
//            progressBar.setVisibility(ProgressBar.GONE);
//        }
//
//        // isi spinner kelas
//        ArrayAdapter<String> kelasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kelasList);
//        spinnerKelas.setAdapter(kelasAdapter);
//
//        // isi spinner kondisi
//        ArrayAdapter<String> kondisiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kondisiList);
//        spinnerKondisi.setAdapter(kondisiAdapter);
//
//        // 🔹 Event: jika kelas dipilih → fetch nama dari server
//        spinnerKelas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
//                String selectedKelas = kelasList[position];
//                fetchNamaByKelas(selectedKelas);
//            }
//
//            @Override
//            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
//        });
//
//        btnPickPhoto.setOnClickListener(v -> openGallery());
//
//        btnSubmit.setOnClickListener(v -> {
//            String nama = etNama.getText().toString().trim();
//            String kelas = spinnerKelas.getSelectedItem().toString();
//
//            if (nama.isEmpty() || kelas.isEmpty()) {
//                Toast.makeText(this, "Nama dan kelas harus diisi", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (!isNetworkAvailable()) {
//                Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            checkBorrowing(nama, kelas);
//        });
//    }
//
//    // 🔹 Ambil nama dari server sesuai kelas
//    private void fetchNamaByKelas(String kelas) {
//        showLoading("Mengambil daftar nama...");
//
//        String url = "http://10.0.2.2/ASPAJ/v1/get_peminjaman.php";
//        StringRequest request = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    hideLoading();
//                    try {
//                        JSONObject obj = new JSONObject(response);
//                        if (obj.getString("status").equals("success")) {
//                            JSONArray arr = obj.getJSONArray("names");
//                            ArrayList<String> listNama = new ArrayList<>();
//                            for (int i = 0; i < arr.length(); i++) {
//                                listNama.add(arr.getString(i));
//                            }
//
//                            ArrayAdapter<String> namaAdapter = new ArrayAdapter<>(
//                                    pengembalian.this,
//                                    android.R.layout.simple_dropdown_item_1line,
//                                    listNama
//                            );
//                            etNama.setAdapter(namaAdapter);
//                            etNama.setText(""); // reset input setiap ganti kelas
//                        } else {
//                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//                            etNama.setAdapter(null);
//                            etNama.setText("");
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                },
//                error -> {
//                    hideLoading();
//                    Toast.makeText(this, "Network error saat ambil nama", Toast.LENGTH_SHORT).show();
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("kelas", kelas);
//                return params;
//            }
//        };
//
//        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(this).add(request);
//    }
//
//    // 🔹 Check network connectivity
//    private boolean isNetworkAvailable() {
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (connectivityManager != null) {
//                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//            }
//            return false;
//        } catch (Exception e) {
//            return true;
//        }
//    }
//
//    // 🔹 Open gallery
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            imgPreview.setImageURI(selectedImageUri);
//            imageFile = new File(getRealPathFromURI(selectedImageUri));
//        }
//    }
//
//    private String getRealPathFromURI(Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor == null) return null;
//        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String filePath = cursor.getString(columnIndex);
//        cursor.close();
//        return filePath;
//    }
//
//    // 🔹 Check borrowing sebelum submit
//    private void checkBorrowing(String nama, String kelas) {
//        showLoading("Checking borrowing data...");
//
//        String url = "http://10.0.2.2/android/v1/checkBorrowing.php";
//        StringRequest checkRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    hideLoading();
//                    Toast.makeText(this, "Server: " + response, Toast.LENGTH_SHORT).show();
//                    if (response.equalsIgnoreCase("exists")) {
//                        String kondisi = spinnerKondisi.getSelectedItem().toString();
//                        updateStatus(nama, kelas, kondisi);
//                    } else {
//                        Toast.makeText(this, "Data tidak ditemukan / tidak approved", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    hideLoading();
//                    Toast.makeText(this, "Error checkBorrowing", Toast.LENGTH_SHORT).show();
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("nama", nama);
//                params.put("kelas", kelas);
//                return params;
//            }
//        };
//
//        checkRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(this).add(checkRequest);
//    }
//
//    // 🔹 Update status jadi returned
//    private void updateStatus(String nama, String kelas, String kondisi) {
//        showLoading("Submitting return data...");
//        // di sini nanti ganti pakai MultipartRequest untuk upload foto
//        // sementara contoh basic StringRequest:
//        String url = "http://10.0.2.2/android/v1/pengembalian.php";
//        StringRequest updateRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    hideLoading();
//                    Toast.makeText(pengembalian.this, "Response: " + response, Toast.LENGTH_LONG).show();
//                    clearForm();
//                },
//                error -> {
//                    hideLoading();
//                    Toast.makeText(this, "Error updateStatus", Toast.LENGTH_SHORT).show();
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("nama", nama);
//                params.put("kelas", kelas);
//                params.put("kondisi", kondisi);
//                params.put("status", "returned");
//                return params;
//            }
//        };
//
//        updateRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(this).add(updateRequest);
//    }
//
//    private void showLoading(String message) {
//        if (progressBar != null) progressBar.setVisibility(ProgressBar.VISIBLE);
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void hideLoading() {
//        if (progressBar != null) progressBar.setVisibility(ProgressBar.GONE);
//    }
//
//    private void clearForm() {
//        etNama.setText("");
//        spinnerKelas.setSelection(0);
//        spinnerKondisi.setSelection(0);
//        imgPreview.setImageResource(android.R.color.transparent);
//        selectedImageUri = null;
//        imageFile = null;
//    }
//}
