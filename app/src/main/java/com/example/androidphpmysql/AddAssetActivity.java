package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddAssetActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddAssetActivity";

    private EditText editTextNamaBarang, editTextKodeBarang, editTextJumlahStok, editTextLokasiBarang,
            editTextMerk, editTextHargaSatuan, editTextSumber, editTextTahun, editTextDeskripsi;
    private Spinner spinnerJurusanBarang;
    private Button buttonSimpan, buttonKembali;
    private ProgressDialog progressDialog;
    private int assetId = -1; // -1 for new asset, otherwise edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(assetId == -1 ? "Tambah Aset" : "Edit Aset");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        editTextNamaBarang = findViewById(R.id.editTextNamaBarang);
        editTextKodeBarang = findViewById(R.id.editTextKodeBarang);
        editTextJumlahStok = findViewById(R.id.editTextJumlahStok);
        editTextLokasiBarang = findViewById(R.id.editTextLokasiBarang);
        spinnerJurusanBarang = findViewById(R.id.spinnerJurusanBarang);
        editTextMerk = findViewById(R.id.editTextMerk);
        editTextHargaSatuan = findViewById(R.id.editTextHargaSatuan);
        editTextSumber = findViewById(R.id.editTextSumber);
        editTextTahun = findViewById(R.id.editTextTahun);
        editTextDeskripsi = findViewById(R.id.editTextDeskripsi);
        buttonSimpan = findViewById(R.id.buttonSimpan);
        buttonKembali = findViewById(R.id.buttonKembali);

        // Set spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jurusan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJurusanBarang.setAdapter(adapter);

        buttonSimpan.setOnClickListener(this);
        buttonKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if editing
        if (getIntent().hasExtra("asset_id")) {
            assetId = getIntent().getIntExtra("asset_id", -1);
            editTextNamaBarang.setText(getIntent().getStringExtra("asset_nama_barang"));
            editTextKodeBarang.setText(getIntent().getStringExtra("asset_kode_barang"));
            editTextJumlahStok.setText(getIntent().getStringExtra("asset_jumlah_stok"));
            editTextLokasiBarang.setText(getIntent().getStringExtra("asset_lokasi_barang"));
            String jurusan = getIntent().getStringExtra("asset_jurusan_barang");
            if (jurusan != null) {
                int spinnerPosition = adapter.getPosition(jurusan);
                spinnerJurusanBarang.setSelection(spinnerPosition);
            }
            editTextMerk.setText(getIntent().getStringExtra("asset_merk"));
            editTextHargaSatuan.setText(getIntent().getStringExtra("asset_harga_satuan"));
            editTextSumber.setText(getIntent().getStringExtra("asset_sumber"));
            editTextTahun.setText(getIntent().getStringExtra("asset_tahun"));
            editTextDeskripsi.setText(getIntent().getStringExtra("asset_deskripsi"));
        }
    }

    private void saveAsset() {
        String namaBarang = editTextNamaBarang.getText().toString().trim();
        String kodeBarang = editTextKodeBarang.getText().toString().trim();
        String jumlahStok = editTextJumlahStok.getText().toString().trim();
        String lokasiBarang = editTextLokasiBarang.getText().toString().trim();
        String jurusanBarang = spinnerJurusanBarang.getSelectedItem() != null ? spinnerJurusanBarang.getSelectedItem().toString() : "";
        String merk = editTextMerk.getText().toString().trim();
        String hargaSatuanStr = editTextHargaSatuan.getText().toString().trim();
        String sumber = editTextSumber.getText().toString().trim();
        String tahun = editTextTahun.getText().toString().trim();
        String deskripsi = editTextDeskripsi.getText().toString().trim();

        if (namaBarang.isEmpty() || kodeBarang.isEmpty() || jumlahStok.isEmpty() || lokasiBarang.isEmpty() ||
                jurusanBarang.isEmpty() || merk.isEmpty() || hargaSatuanStr.isEmpty() || sumber.isEmpty() ||
                tahun.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        double hargaSatuan;
        try {
            hargaSatuan = Double.parseDouble(hargaSatuanStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Harga Satuan harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Saving asset...");
        progressDialog.show();

        String url = (assetId == -1) ? Constants.URL_ADD_ASSET : Constants.URL_UPDATE_ASSET;
        int method = Request.Method.POST;

        StringRequest stringRequest = new StringRequest(
                method,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "SAVE_RESPONSE: " + response);

                        // Validate response before parsing JSON
                        if (response == null || response.trim().isEmpty()) {
                            Log.e(TAG, "Empty response from server");
                            Toast.makeText(getApplicationContext(),
                                    "Empty response from server", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if response is HTML (error page) instead of JSON
                        String trimmedResponse = response.trim();
                        if (trimmedResponse.startsWith("<") || trimmedResponse.contains("<!DOCTYPE") ||
                            trimmedResponse.contains("<html") || trimmedResponse.contains("<br")) {
                            Log.e(TAG, "Server returned HTML instead of JSON: " + response);
                            Toast.makeText(getApplicationContext(),
                                    "Server error: Database connection failed. Please check server configuration.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(trimmedResponse);

                            // Check if response has the expected format
                            if (!jsonObject.has("success")) {
                                Log.e(TAG, "Invalid response format - missing 'success' field");
                                Toast.makeText(getApplicationContext(),
                                        "Invalid response format from server", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String message = jsonObject.optString("message", "Unknown error");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            if (jsonObject.getBoolean("success")) {
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error", e);
                            Toast.makeText(getApplicationContext(),
                                    "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        String errorMessage = "Error saving asset";
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            try {
                                String responseBody = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                                JSONObject data = new JSONObject(responseBody);
                                if (data.has("message")) {
                                    errorMessage = data.getString("message");
                                }
                            } catch (UnsupportedEncodingException | JSONException e) {
                                Log.e(TAG, "Error parsing error response", e);
                            }
                        }
                        Log.e(TAG, "Volley error", error);
                        Toast.makeText(getApplicationContext(),
                                errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", namaBarang);
                params.put("deskripsi", deskripsi);
                params.put("jurusan", jurusanBarang);
                params.put("harga_satuan", String.valueOf(hargaSatuan));
                // Add other fields to match PHP API
                params.put("code", kodeBarang);
                params.put("stock", jumlahStok);
                params.put("lokasi", lokasiBarang);
                params.put("merk", merk);
                params.put("sumber", sumber);
                params.put("tahun", tahun);
                params.put("condition", "Baik"); // default condition value
                if (assetId != -1) {
                    params.put("id", String.valueOf(assetId));
                }
                return params;
            }
        };

        // Add retry policy to handle timeout and retries
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSimpan) {
            saveAsset();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
