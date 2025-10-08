package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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

    public void loadAssets() {
        assetList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_ASSETS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        JSONArray assets = obj.getJSONArray("commodities");

                        for (int i = 0; i < assets.length(); i++) {
                            JSONObject assetObject = assets.getJSONObject(i);

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

                            Asset asset = new Asset(id, namaBarang, kodeBarang, jumlahStok, lokasiBarang, jurusanBarang, merk, hargaSatuan, sumber, tahun, deskripsi);

                            assetList.add(asset);
                        }

                        adapter = new AssetAdapter(AssetListActivity.this, assetList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Network error occurred";
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}