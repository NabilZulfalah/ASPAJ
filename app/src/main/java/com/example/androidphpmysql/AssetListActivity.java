package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssetAdapter adapter;
    private List<Asset> assetList;
    private Button buttonAddAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        recyclerView = findViewById(R.id.recyclerViewAssets);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonAddAsset = findViewById(R.id.buttonAddAsset);
        buttonAddAsset.setOnClickListener(v -> {
            Intent intent = new Intent(AssetListActivity.this, AddAssetActivity.class);
            startActivity(intent);
        });

        assetList = new ArrayList<>();

        loadAssets();
    }

    /**
     * Method untuk memuat daftar aset dari API Laravel menggunakan Volley.
     * Menggunakan JsonObjectRequest untuk mendapatkan response JSON dari endpoint /api/assets.
     * Response Laravel memiliki struktur: {success: boolean, message: string, data: JSONArray}
     */
    public void loadAssets() {
        // Kosongkan list aset sebelum memuat data baru
        assetList.clear();

        // Log untuk menandai mulai loading aset
        Log.d("AssetListActivity", "Memulai loading aset dari API Laravel");

        // Membuat JsonObjectRequest untuk GET request ke endpoint /api/assets
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, // Method HTTP GET
                Constants.GET_ASSETS_URL, // URL endpoint dari Constants
                null, // Body request null karena GET tidak memerlukan body
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Callback ketika response berhasil diterima
                        try {
                            // Parsing response JSON Laravel
                            boolean success = response.getBoolean("success"); // Cek apakah request berhasil
                            String message = response.getString("message"); // Pesan dari server

                            Log.d("AssetListActivity", "Response success: " + success + ", message: " + message);

                            if (success) {
                                // Jika berhasil, ambil array data aset
                                JSONArray data = response.getJSONArray("data");

                                // Loop melalui setiap objek aset dalam array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject assetObject = data.getJSONObject(i);

                                    // Ekstrak field-field dari objek aset
                                    int id = assetObject.getInt("id");
                                    String namaBarang = assetObject.optString("name", "");
                                    String kodeBarang = assetObject.optString("code", "");
                                    String jumlahStok = String.valueOf(assetObject.optInt("stock", 0));
                                    String lokasiBarang = assetObject.optString("lokasi", "");
                                    String jurusanBarang = assetObject.optString("jurusan", "");
                                    String merk = assetObject.optString("merk", "");
                                    double hargaSatuan = assetObject.isNull("harga_satuan") ? 0.0 : assetObject.getDouble("harga_satuan");
                                    String sumber = assetObject.optString("sumber", "");
                                    String tahun = assetObject.isNull("tahun") ? "" : String.valueOf(assetObject.getInt("tahun"));
                                    String deskripsi = assetObject.optString("deskripsi", "");

                                    // Buat objek Asset dari data yang diekstrak
                                    Asset asset = new Asset(id, namaBarang, kodeBarang, jumlahStok, lokasiBarang, jurusanBarang, merk, hargaSatuan, sumber, tahun, deskripsi);

                                    // Tambahkan aset ke list
                                    assetList.add(asset);

                                    // Log detail aset untuk debugging
                                    Log.d("AssetListActivity", "Aset dimuat: " + namaBarang + " (ID: " + id + ")");
                                }

                                // Set adapter untuk RecyclerView dan tampilkan data
                                adapter = new AssetAdapter(AssetListActivity.this, assetList);
                                recyclerView.setAdapter(adapter);

                                // Log jumlah aset yang berhasil dimuat
                                Log.d("AssetListActivity", "Total aset dimuat: " + assetList.size());
                            } else {
                                // Jika success false, tampilkan pesan error dari server
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Log.e("AssetListActivity", "Gagal memuat aset: " + message);
                            }
                        } catch (JSONException e) {
                            // Tangani error parsing JSON
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AssetListActivity", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Callback ketika terjadi error network atau server
                        String errorMsg = error.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Network error occurred";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("AssetListActivity", "Error response: " + errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPrefManager.getInstance(AssetListActivity.this).getToken();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        // Tambahkan request ke RequestQueue menggunakan VolleySingleton
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}