package com.example.androidphpmysql;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BorrowConfirmActivity extends AppCompatActivity {

    private TextView textViewSelectedItems;
    private EditText editTextTujuan, editTextReturnDate;
    private Button buttonSubmitBorrow;
    private String cartJson;
    private String selectedItemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_confirm);

        textViewSelectedItems = findViewById(R.id.textViewSelectedItems);
        editTextTujuan = findViewById(R.id.editTextTujuan);
        editTextReturnDate = findViewById(R.id.editTextReturnDate);
        buttonSubmitBorrow = findViewById(R.id.buttonSubmitBorrow);

        cartJson = getIntent().getStringExtra("cart");
        selectedItemsText = getIntent().getStringExtra("selected_items_text");
        if (selectedItemsText != null) {
            textViewSelectedItems.setText(selectedItemsText);
        }

        editTextReturnDate.setOnClickListener(v -> showDatePicker());

        buttonSubmitBorrow.setOnClickListener(v -> submitBorrow());

        // Set default return date to tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String defaultDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
        editTextReturnDate.setText(defaultDate);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
            editTextReturnDate.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void submitBorrow() {
        if (cartJson == null || cartJson.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String tujuan = editTextTujuan.getText().toString().trim();
        if (tujuan.isEmpty()) {
            Toast.makeText(this, "Masukkan alasan peminjaman", Toast.LENGTH_SHORT).show();
            return;
        }

        String returnDate = editTextReturnDate.getText().toString().trim();
        if (returnDate.isEmpty()) {
            Toast.makeText(this, "Pilih tanggal pengembalian", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date for borrow_date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String borrowDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);

        try {
            JSONArray originalItems = new JSONArray(cartJson);
            JSONArray itemsArray = new JSONArray();
            for (int i = 0; i < originalItems.length(); i++) {
                JSONObject item = originalItems.getJSONObject(i);
                JSONObject newItem = new JSONObject();
                newItem.put("commodity_id", item.getInt("asset_id"));
                newItem.put("quantity", item.getInt("quantity"));
                itemsArray.put(newItem);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("borrow_date", borrowDate);
            jsonObject.put("return_date", returnDate);
            jsonObject.put("tujuan", tujuan);
            jsonObject.put("items", itemsArray);

            String url = Constants.BASE_URL + "borrowings";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(BorrowConfirmActivity.this, "Peminjaman berhasil diajukan", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(BorrowConfirmActivity.this, "Gagal: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
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
                        Toast.makeText(BorrowConfirmActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(BorrowConfirmActivity.this).getToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
        }
    }
}
