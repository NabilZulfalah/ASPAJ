package com.example.androidphpmysql;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.content.Intent;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetBorrowActivity extends AppCompatActivity implements AssetAdapter.QuantityChangeListener {

    private Spinner spinnerJurusan;
    private EditText editTextSearch, editTextTujuan, editTextReturnDate;
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
        editTextTujuan = findViewById(R.id.editTextTujuan);
        editTextReturnDate = findViewById(R.id.editTextReturnDate);

        recyclerViewAssets.setLayoutManager(new GridLayoutManager(this, 2));
        assetAdapter = new AssetAdapter(this, assetList, this);
        recyclerViewAssets.setAdapter(assetAdapter);

        editTextReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAssets();
            }
        });

        buttonSubmitBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.isEmpty()) {
                    Toast.makeText(AssetBorrowActivity.this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Build JSONArray for cart
                JSONArray itemsArray = new JSONArray();
                StringBuilder selectedText = new StringBuilder("Selected Items:\n");
                try {
                    for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                        JSONObject item = new JSONObject();
                        item.put("asset_id", entry.getKey());
                        item.put("quantity", entry.getValue());
                        itemsArray.put(item);
                        selectedText.append("Asset ID ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" unit(s)\n");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                Intent intent = new Intent(AssetBorrowActivity.this, BorrowConfirmActivity.class);
                intent.putExtra("cart", itemsArray.toString());
                intent.putExtra("selected_items_text", selectedText.toString());
                startActivityForResult(intent, 1);
            }
        });

        // Load initial assets
        loadAssets();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cart.clear();
            assetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQuantityChange(int assetId, int quantity) {
        if (quantity > 0) {
            cart.put(assetId, quantity);
        } else {
            cart.remove(assetId);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                editTextReturnDate.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void submitBorrow() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String tujuan = editTextTujuan.getText().toString().trim();
        if (tujuan.isEmpty()) {
            Toast.makeText(this, "Masukkan tujuan peminjaman", Toast.LENGTH_SHORT).show();
            return;
        }

        String returnDate = editTextReturnDate.getText().toString().trim();
        if (returnDate.isEmpty()) {
            Toast.makeText(this, "Pilih tanggal pengembalian", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build JSON
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray itemsArray = new JSONArray();
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                JSONObject item = new JSONObject();
                item.put("asset_id", entry.getKey());
                item.put("quantity", entry.getValue());
                itemsArray.put(item);
            }
            jsonObject.put("items", itemsArray);
            jsonObject.put("tujuan", tujuan);
            jsonObject.put("return_date", returnDate);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = Constants.BASE_URL + "borrowings";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(AssetBorrowActivity.this, "Peminjaman berhasil diajukan", Toast.LENGTH_SHORT).show();
                                cart.clear();
                                assetAdapter.notifyDataSetChanged();
                                editTextTujuan.setText("");
                                editTextReturnDate.setText("");
                            } else {
                                Toast.makeText(AssetBorrowActivity.this, "Gagal: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error submitting borrow";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonError = new JSONObject(responseBody);
                                if (jsonError.has("message")) {
                                    errorMessage = jsonError.getString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(AssetBorrowActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(AssetBorrowActivity.this).getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
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
