package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

public class BorrowingDetailActivity extends AppCompatActivity {

    private TextView tvBorrowingId, tvBorrowDate, tvReturnDate, tvPurpose, tvBorrowedBy;
    private RecyclerView rvBorrowedItems;
    private Toolbar toolbar;
    private List<BorrowedItem> borrowedItemsList;
    private BorrowedItemAdapter borrowedItemAdapter;

    private static final String BASE_URL = Constants.BASE_URL;
    private String borrowingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_detail);

        // Get borrowing ID from intent
        borrowingId = getIntent().getStringExtra("borrowing_id");
        if (borrowingId == null) {
            Toast.makeText(this, "No borrowing ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        fetchBorrowingDetails();
    }

    private void initViews() {
        tvBorrowingId = findViewById(R.id.tvBorrowingId);
        tvBorrowDate = findViewById(R.id.tvBorrowDate);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        tvPurpose = findViewById(R.id.tvPurpose);
        tvBorrowedBy = findViewById(R.id.tvBorrowedBy);
        rvBorrowedItems = findViewById(R.id.rvBorrowedItems);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Peminjaman");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        borrowedItemsList = new ArrayList<>();
        borrowedItemAdapter = new BorrowedItemAdapter(borrowedItemsList, borrowingId);
        rvBorrowedItems.setLayoutManager(new LinearLayoutManager(this));
        rvBorrowedItems.setAdapter(borrowedItemAdapter);
    }

    private void fetchBorrowingDetails() {
        android.util.Log.d("BorrowingDetail", "Borrowing ID: " + borrowingId);
        String token = SharedPrefManager.getInstance(this).getToken();
        android.util.Log.d("BorrowingDetail", "Token: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url;
        try {
            String encodedId = URLEncoder.encode(borrowingId, "UTF-8");
            url = BASE_URL + "borrowings/" + encodedId;
            android.util.Log.d("BorrowingDetail", "URL: " + url);
        } catch (Exception e) {
            android.util.Log.e("BorrowingDetail", "Encoding error: " + e.getMessage());
            Toast.makeText(this, "Error encoding borrowing ID", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        android.util.Log.d("BorrowingDetail", "Response received: " + jsonObject.toString());
                        try {
                            if (jsonObject.getBoolean("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                tvBorrowingId.setText(data.getString("id"));
                                tvBorrowDate.setText(data.getString("borrow_date"));
                                tvReturnDate.setText(data.getString("return_date"));
                                tvPurpose.setText(data.getString("tujuan"));
                                JSONObject student = data.getJSONObject("student");
                                String studentName = student.getString("name");
                                String borrowTime = data.getString("borrow_date");
                                tvBorrowedBy.setText(studentName + " - " + borrowTime);

                                JSONArray itemsArray = data.getJSONArray("items");
                                borrowedItemsList.clear();
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject item = itemsArray.getJSONObject(i);
                                    JSONObject commodity = item.getJSONObject("commodity");
                                    String status = item.getString("status");
                                    String note = "";
                                    if ("approved".equals(status)) {
                                        String lokasi = commodity.optString("lokasi", "");
                                        if (!lokasi.isEmpty()) {
                                            note = "Silakan ambil barang di " + lokasi + " segera.";
                                        }
                                    }
                                    BorrowedItem borrowedItem = new BorrowedItem(
                                            item.getInt("id"),
                                            commodity.getString("code"),
                                            commodity.getString("name"),
                                            status,
                                            item.getInt("quantity"),
                                            item.optString("stock_info", ""),
                                            note
                                    );
                                    borrowedItemsList.add(borrowedItem);
                                }
                                borrowedItemAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(BorrowingDetailActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BorrowingDetailActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.e("BorrowingDetail", "VolleyError: " + error.toString(), error);
                String errorMessage = "Unknown error";
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    switch (statusCode) {
                        case 401:
                            errorMessage = "Unauthorized: Please login again.";
                            break;
                        case 403:
                            errorMessage = "Forbidden: You don't have permission to view this borrowing.";
                            break;
                        case 404:
                            errorMessage = "Borrowing not found.";
                            break;
                        case 500:
                            errorMessage = "Server error: Please try again later.";
                            break;
                        default:
                            errorMessage = "Error " + statusCode + ": " + (error.getMessage() != null ? error.getMessage() : "Unknown");
                            break;
                    }
                } else {
                    if (error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                }
                Toast.makeText(BorrowingDetailActivity.this, "Error fetching data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        android.util.Log.d("BorrowingDetail", "Request queued");
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }



    // Model class for borrowed items
    public static class BorrowedItem {
        private int itemId;
        private String code;
        private String name;
        private String status;
        private int quantity;
        private String stockInfo;
        private String note;

        public BorrowedItem(int itemId, String code, String name, String status, int quantity, String stockInfo, String note) {
            this.itemId = itemId;
            this.code = code;
            this.name = name;
            this.status = status;
            this.quantity = quantity;
            this.stockInfo = stockInfo;
            this.note = note;
        }

        // Getters
        public int getItemId() { return itemId; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public int getQuantity() { return quantity; }
        public String getStockInfo() { return stockInfo; }
        public String getNote() { return note; }
    }

    // Adapter for borrowed items
    public static class BorrowedItemAdapter extends RecyclerView.Adapter<BorrowedItemAdapter.ViewHolder> {
        private List<BorrowedItem> items;
        private String borrowingId;

        public BorrowedItemAdapter(List<BorrowedItem> items, String borrowingId) {
            this.items = items;
            this.borrowingId = borrowingId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrowed_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BorrowedItem item = items.get(position);
            holder.tvCode.setText(item.getCode());
            holder.tvName.setText(item.getName());
            holder.tvQuantity.setText("Qty " + item.getQuantity());
            holder.tvStatus.setText(item.getStatus().toUpperCase());

            // Set status color
            int statusColor = android.graphics.Color.GRAY;
            switch (item.getStatus().toLowerCase()) {
                case "returned":
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case "rejected":
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
                    break;
                case "approved":
                    statusColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark);
                    break;
            }
            holder.tvStatus.setBackgroundColor(statusColor);
            holder.tvStatus.setTextColor(android.graphics.Color.WHITE);

            if (!item.getStockInfo().isEmpty()) {
                holder.tvStockInfo.setVisibility(View.VISIBLE);
                holder.tvStockInfo.setText(item.getStockInfo());
            } else {
                holder.tvStockInfo.setVisibility(View.GONE);
            }

            if (!item.getNote().isEmpty()) {
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setText(item.getNote());
            } else {
                holder.tvNote.setVisibility(View.GONE);
            }

            if ("approved".equals(item.getStatus().toLowerCase())) {
                holder.btnReturnItem.setVisibility(View.VISIBLE);
                holder.btnReturnItem.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ReturnFormActivity.class);
                    intent.putExtra("borrowing_id", borrowingId);
                    intent.putExtra("item_id", item.getItemId());
                    holder.itemView.getContext().startActivity(intent);
                });
            } else {
                holder.btnReturnItem.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCode, tvName, tvStatus, tvQuantity, tvStockInfo, tvNote;
            Button btnReturnItem;

            public ViewHolder(View itemView) {
                super(itemView);
                tvCode = itemView.findViewById(R.id.tvCode);
                tvName = itemView.findViewById(R.id.tvName);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvStockInfo = itemView.findViewById(R.id.tvStockInfo);
                tvNote = itemView.findViewById(R.id.tvNote);
                btnReturnItem = itemView.findViewById(R.id.btnReturnItem);
            }
        }
    }
}
