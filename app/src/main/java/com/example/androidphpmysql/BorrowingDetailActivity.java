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
    private int borrowingId;
    private String borrowingStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_detail);

        // Get borrowing ID from intent
        String borrowingIdStr = getIntent().getStringExtra("borrowing_id");
        borrowingId = borrowingIdStr != null ? Integer.parseInt(borrowingIdStr) : -1;
        if (borrowingId == -1) {
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
        borrowedItemAdapter = new BorrowedItemAdapter(borrowedItemsList, String.valueOf(borrowingId), borrowingStatus);
        rvBorrowedItems.setLayoutManager(new LinearLayoutManager(this));
        rvBorrowedItems.setAdapter(borrowedItemAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int returnedItemId = data.getIntExtra("returned_item_id", -1);
            if (returnedItemId != -1) {
                // Find the item by ID and update its status
                for (int i = 0; i < borrowedItemsList.size(); i++) {
                    if (borrowedItemsList.get(i).getItemId() == returnedItemId) {
                        borrowedItemsList.get(i).setStatus("returned");
                        borrowedItemAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
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
            String encodedId = URLEncoder.encode(String.valueOf(borrowingId), "UTF-8");
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

                                borrowingStatus = data.getString("status");
                                android.util.Log.d("BorrowingDetail", "Borrowing Status: " + borrowingStatus);

                                JSONArray itemsArray = data.getJSONArray("items");
                                borrowedItemsList.clear();
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject item = itemsArray.getJSONObject(i);
                                    JSONObject commodity = item.getJSONObject("commodity");
                                    String status = item.getString("status");
                                    android.util.Log.d("BorrowingDetail", "Item " + i + " Status: " + status);
                                    String note = "";
                                    if ("approved".equals(status)) {
                                        String lokasi = commodity.optString("lokasi", "");
                                        if (!lokasi.isEmpty()) {
                                            note = "Silakan ambil barang di " + lokasi + " segera.";
                                        }
                                    }
                                    String returnPhotoUrl = item.optString("return_photo", "");
                                    BorrowedItem borrowedItem = new BorrowedItem(
                                            item.getInt("id"),
                                            commodity.getString("code"),
                                            commodity.getString("name"),
                                            status,
                                            item.getInt("quantity"),
                                            item.optString("stock_info", ""),
                                            note,
                                            returnPhotoUrl
                                    );
                                    borrowedItemsList.add(borrowedItem);
                                }
                                borrowedItemAdapter = new BorrowedItemAdapter(borrowedItemsList, String.valueOf(borrowingId), borrowingStatus);
                                rvBorrowedItems.setAdapter(borrowedItemAdapter);
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
        private android.graphics.Bitmap returnPhoto;
        private String returnPhotoUrl;

        public BorrowedItem(int itemId, String code, String name, String status, int quantity, String stockInfo, String note, String returnPhotoUrl) {
            this.itemId = itemId;
            this.code = code;
            this.name = name;
            this.status = status;
            this.quantity = quantity;
            this.stockInfo = stockInfo;
            this.note = note;
            this.returnPhoto = null;
            this.returnPhotoUrl = returnPhotoUrl;
        }

        // Getters
        public int getItemId() { return itemId; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public int getQuantity() { return quantity; }
        public String getStockInfo() { return stockInfo; }
        public String getNote() { return note; }
        public android.graphics.Bitmap getReturnPhoto() { return returnPhoto; }
        public String getReturnPhotoUrl() { return returnPhotoUrl; }
        public void setReturnPhoto(android.graphics.Bitmap returnPhoto) { this.returnPhoto = returnPhoto; }
        public void setStatus(String status) { this.status = status; }
    }

    // Adapter for borrowed items
    public static class BorrowedItemAdapter extends RecyclerView.Adapter<BorrowedItemAdapter.ViewHolder> {
        private List<BorrowedItem> items;
        private String borrowingId;

        public BorrowedItemAdapter(List<BorrowedItem> items, String borrowingId, String borrowingStatus) {
            this.items = items;
            this.borrowingId = borrowingId;
            this.borrowingStatus = borrowingStatus;
        }

        private String borrowingStatus;

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

            // Show return button only if item is approved or borrowed and borrowing is not completed
            // Or show view return proof button if item is returned
            if (("approved".equals(item.getStatus().toLowerCase()) || "borrowed".equals(item.getStatus().toLowerCase())) &&
                !"completed".equals(borrowingStatus.toLowerCase())) {
                holder.btnReturnItem.setVisibility(View.VISIBLE);
                holder.btnReturnItem.setText("Kembalikan");
                holder.btnReturnItem.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ReturnFormActivity.class);
                    intent.putExtra("borrowing_id", borrowingId);
                    intent.putExtra("item_id", String.valueOf(item.getItemId()));
                    ((android.app.Activity) holder.itemView.getContext()).startActivityForResult(intent, 1);
                });
            } else if ("returned".equals(item.getStatus().toLowerCase())) {
                String returnPhotoUrl = item.getReturnPhotoUrl();
                if (returnPhotoUrl != null && !returnPhotoUrl.isEmpty()) {
                    holder.btnReturnItem.setVisibility(View.VISIBLE);
                    holder.btnReturnItem.setText("Lihat Bukti Pengembalian");
                    holder.btnReturnItem.setOnClickListener(v -> {
                        Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                        intent.putExtra("image_url", returnPhotoUrl);
                        holder.itemView.getContext().startActivity(intent);
                    });
                } else {
                    holder.btnReturnItem.setVisibility(View.GONE);
                }
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
