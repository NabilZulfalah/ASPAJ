package com.example.androidphpmysql;

import android.os.Bundle;
import android.view.View;
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

public class BorrowingStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBorrowings;
    private BorrowingCardAdapter borrowingCardAdapter;
    private List<Borrowing> borrowingList = new ArrayList<>();
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_status);

        recyclerViewBorrowings = findViewById(R.id.recyclerViewBorrowings);
        emptyStateText = findViewById(R.id.emptyStateText);
        recyclerViewBorrowings.setLayoutManager(new LinearLayoutManager(this));
        borrowingCardAdapter = new BorrowingCardAdapter(this, borrowingList);
        recyclerViewBorrowings.setAdapter(borrowingCardAdapter);

        loadBorrowings();
    }

    private void loadBorrowings() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.STUDENT_BORROWINGS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        borrowingList.clear();
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                Borrowing borrowing = new Borrowing(
                                        obj.getString("id"),
                                        obj.getString("status"),
                                        obj.getString("borrow_date"),
                                        obj.getString("return_date"),
                                        obj.getString("tujuan"),
                                        obj.getJSONArray("items")
                                );
                                borrowingList.add(borrowing);
                            }
                            borrowingCardAdapter.notifyDataSetChanged();
                            updateEmptyState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BorrowingStatusActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BorrowingStatusActivity.this, "Error loading borrowings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(BorrowingStatusActivity.this).getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateEmptyState() {
        if (borrowingList.isEmpty()) {
            recyclerViewBorrowings.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("Belum ada riwayat peminjaman");
        } else {
            recyclerViewBorrowings.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }
}
