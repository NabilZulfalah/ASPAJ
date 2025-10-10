package com.example.androidphpmysql;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetBorrowActivity extends AppCompatActivity implements AssetAdapter.QuantityChangeListener {

    private Spinner spinnerJurusan;
    private EditText editTextSearch;
    private Button buttonSearch, buttonSubmitBorrow;
    private RecyclerView recyclerViewAssets;
    private AssetAdapter assetAdapter;
    private List<Asset> assetList = new ArrayList<>();
    private Map<Integer, Integer> cart = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_borrow);

        spinnerJurusan = findViewById(R.id.spinnerJurusan);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSubmitBorrow = findViewById(R.id.buttonSubmitBorrow);
        recyclerViewAssets = findViewById(R.id.recyclerViewAssets);

        recyclerViewAssets.setLayoutManager(new GridLayoutManager(this, 2));
        assetAdapter = new AssetAdapter(this, assetList, this);
        recyclerViewAssets.setAdapter(assetAdapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAssets();
            }
        });

        buttonSubmitBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBorrow();
            }
        });

        // Load initial assets
        loadAssets();
    }

    @Override
    public void onQuantityChange(int assetId, int quantity) {
        if (quantity > 0) {
            cart.put(assetId, quantity);
        } else {
            cart.remove(assetId);
        }
    }

    private void submitBorrow() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implement borrow submission logic here
        Toast.makeText(this, "Borrow submitted", Toast.LENGTH_SHORT).show();
    }

    private void loadAssets() {
        String jurusan = spinnerJurusan.getSelectedItem().toString();
        String search = editTextSearch.getText().toString().trim();

        String url = Constants.BASE_URL + "commodities?jurusan=" + jurusan + "&search=" + search;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");
                            assetList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Asset asset = new Asset(
                                        obj.getInt("id"),
                                        obj.getString("name"),
                                        "", // kodeBarang
                                        String.valueOf(obj.getInt("stock")),
                                        "", // lokasiBarang
                                        "", // jurusanBarang
                                        "", // merk
                                        0.0, // hargaSatuan
                                        "", // sumber
                                        "", // tahun
                                        obj.getString("description")
                                );
                                assetList.add(asset);
                            }
                            assetAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AssetBorrowActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AssetBorrowActivity.this, "Error loading assets", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(AssetBorrowActivity.this).getToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
