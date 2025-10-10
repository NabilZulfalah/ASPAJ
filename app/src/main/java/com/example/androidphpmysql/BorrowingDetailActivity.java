package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class BorrowingDetailActivity extends AppCompatActivity {

    private TextView detailId, detailBorrowDate, detailReturnDate, detailTujuan;
    private RecyclerView recyclerViewItems;
    private BorrowingItemAdapter borrowingItemAdapter;
    private List<BorrowingItem> itemList = new ArrayList<>();
    private Button returnButton;
    private int borrowingId;
    private String borrowingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_detail);

        borrowingId = getIntent().getIntExtra("borrowing_id", -1);
        if (borrowingId == -1) {
            Toast.makeText(this, "Invalid borrowing ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detailId = findViewById(R.id.detailId);
        detailBorrowDate = findViewById(R.id.detailBorrowDate);
        detailReturnDate = findViewById(R.id.detailReturnDate);
        detailTujuan = findViewById(R.id.detailTujuan);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        returnButton = findViewById(R.id.returnButton);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        borrowingItemAdapter = new BorrowingItemAdapter(this, itemList, borrowingId);
        recyclerViewItems.setAdapter(borrowingItemAdapter);

        loadBorrowingDetails();
    }

    private void loadBorrowingDetails() {
        String url = Constants.BASE_URL + "borrowings/" + borrowingId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Check if response has "data" key (single object) or is the data directly
                            JSONObject data;
                            if (response.has("data")) {
                                data = response.getJSONObject("data");
                            } else {
                                data = response;
                            }

                            detailId.setText("#" + data.getInt("id"));
                            detailBorrowDate.setText("Tanggal Peminjaman: " + data.getString("borrow_date"));
                            detailReturnDate.setText("Tanggal Kembali: " + (data.isNull("return_date") ? "-" : data.getString("return_date")));
                            detailTujuan.setText("Tujuan: " + data.getString("tujuan"));
                            borrowingStatus = data.getString("status");

                            itemList.clear();
                            JSONArray itemsArray = data.getJSONArray("items");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject itemObj = itemsArray.getJSONObject(i);
                                JSONObject commodity = itemObj.getJSONObject("commodity");
                                String photoUrl = commodity.optString("photo", null);
                                BorrowingItem item = new BorrowingItem(
                                        itemObj.getInt("id"),
                                        commodity.getString("name"),
                                        commodity.getString("code"),
                                        itemObj.getInt("quantity"),
                                        itemObj.getString("status"),
                                        photoUrl
                                );
                                itemList.add(item);
                            }
                            borrowingItemAdapter.notifyDataSetChanged();

                            // Show return button if borrowing has approved or borrowed items
                            boolean hasReturnableItems = false;
                            for (BorrowingItem item : itemList) {
                                if ("approved".equals(item.getStatus()) || "borrowed".equals(item.getStatus())) {
                                    hasReturnableItems = true;
                                    break;
                                }
                            }

                            if (hasReturnableItems) {
                                returnButton.setVisibility(View.VISIBLE);
                                returnButton.setOnClickListener(v -> {
                                    // Find the first returnable item
                                    int returnableItemId = -1;
                                    for (BorrowingItem item : itemList) {
                                        if ("approved".equals(item.getStatus()) || "borrowed".equals(item.getStatus())) {
                                            returnableItemId = item.getId();
                                            break;
                                        }
                                    }

                                    if (returnableItemId != -1) {
                                        Intent intent = new Intent(BorrowingDetailActivity.this, ReturnFormActivity.class);
                                        intent.putExtra("borrowing_id", borrowingId);
                                        intent.putExtra("item_id", returnableItemId);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(BorrowingDetailActivity.this, "No returnable items found.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                returnButton.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BorrowingDetailActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BorrowingDetailActivity.this, "Error loading details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(BorrowingDetailActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    // Inner class for item model
    public static class BorrowingItem {
        private int id;
        private String name;
        private String code;
        private int quantity;
        private String status;
        private String photoUrl;

        public BorrowingItem(int id, String name, String code, int quantity, String status, String photoUrl) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.quantity = quantity;
            this.status = status;
            this.photoUrl = photoUrl;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCode() { return code; }
        public int getQuantity() { return quantity; }
        public String getStatus() { return status; }
        public String getPhotoUrl() { return photoUrl; }
    }
}
