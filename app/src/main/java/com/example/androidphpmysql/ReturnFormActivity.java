/*  */package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidphpmysql.Constants;
import com.example.androidphpmysql.SharedPrefManager;
import com.example.androidphpmysql.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnFormActivity extends AppCompatActivity {

    private Spinner spinnerCondition;
    private RecyclerView recyclerViewReturnItems;
    private Button buttonSubmitReturn;
    private ProgressDialog progressDialog;
    private int borrowingId;
    private int itemId;
    private List<BorrowingDetailActivity.BorrowedItem> returnItems;
    private ReturnItemAdapter returnItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_form);

        spinnerCondition = findViewById(R.id.spinnerCondition);
        recyclerViewReturnItems = findViewById(R.id.recyclerViewReturnItems);
        buttonSubmitReturn = findViewById(R.id.buttonSubmitReturn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Get borrowing ID and item ID from intent
        String borrowingIdStr = getIntent().getStringExtra("borrowing_id");
        borrowingId = borrowingIdStr != null ? Integer.parseInt(borrowingIdStr) : -1;
        String itemIdStr = getIntent().getStringExtra("item_id");
        itemId = itemIdStr != null ? Integer.parseInt(itemIdStr) : -1;

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.condition_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(adapter);

        // Initialize return items list
        returnItems = new ArrayList<>();

        // Set up RecyclerView
        returnItemAdapter = new ReturnItemAdapter(this, returnItems);
        recyclerViewReturnItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReturnItems.setAdapter(returnItemAdapter);

        buttonSubmitReturn.setOnClickListener(v -> submitReturn());

        // Fetch borrowing details to populate return items
        fetchBorrowingDetailsForReturn();
    }

    private void fetchBorrowingDetailsForReturn() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url;
        try {
            String encodedId = URLEncoder.encode(String.valueOf(borrowingId), "UTF-8");
            url = Constants.BASE_URL + "borrowings/" + encodedId;
        } catch (Exception e) {
            Toast.makeText(this, "Error encoding borrowing ID", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getBoolean("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                JSONArray itemsArray = data.getJSONArray("items");
                                returnItems.clear();
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject item = itemsArray.getJSONObject(i);
                                    JSONObject commodity = item.getJSONObject("commodity");
                                    String status = item.getString("status");
                                    if ("approved".equals(status) && item.getInt("id") == itemId) {
                                        BorrowingDetailActivity.BorrowedItem borrowedItem = new BorrowingDetailActivity.BorrowedItem(
                                                item.getInt("id"),
                                                commodity.getString("code"),
                                                commodity.getString("name"),
                                                status,
                                                item.getInt("quantity"),
                                                item.optString("stock_info", ""),
                                                "",
                                                ""
                                        );
                                        returnItems.add(borrowedItem);
                                        break; // Only add the specific item
                                    }
                                }
                                returnItemAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ReturnFormActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReturnFormActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Unknown error";
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    switch (statusCode) {
                        case 401:
                            errorMessage = "Unauthorized: Please login again.";
                            break;
                        case 403:
                            errorMessage = "Forbidden: You don't have permission.";
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
                Toast.makeText(ReturnFormActivity.this, "Error fetching data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private void submitReturn() {
        if (borrowingId == -1) {
            Toast.makeText(this, "Invalid borrowing ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi tanggal pengembalian
        String borrowDateStr = getIntent().getStringExtra("borrow_date");
        if (borrowDateStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date borrowDate = sdf.parse(borrowDateStr);
                Date currentDate = new Date();

                if (!currentDate.after(borrowDate)) {
                    Toast.makeText(this, "Tanggal pengembalian harus lebih baru dari tanggal peminjaman", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Format tanggal tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String condition = spinnerCondition.getSelectedItem().toString();
        String description = ""; // Optional description

        progressDialog.setMessage("Submitting return...");
        progressDialog.show();

        String url = Constants.BASE_URL + "borrowings/" + borrowingId + "/items/" + itemId + "/return";

        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(ReturnFormActivity.this, "Barang berhasil dikembalikan", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
                    toast.show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("returned_item_id", itemId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMessage = "Error submitting return";
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        switch (statusCode) {
                            case 400:
                                errorMessage += ": Bad request";
                                break;
                            case 401:
                                errorMessage += ": Unauthorized";
                                break;
                            case 403:
                                errorMessage += ": Forbidden";
                                break;
                            case 404:
                                errorMessage += ": Not found";
                                break;
                            case 500:
                                errorMessage += ": Server error";
                                break;
                            default:
                                errorMessage += ": " + statusCode;
                                break;
                        }
                    } else if (error.getMessage() != null) {
                        errorMessage += ": " + error.getMessage();
                    } else {
                        errorMessage += ": Unknown error";
                    }
                    Toast.makeText(ReturnFormActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("return_condition", condition);
                params.put("item_id", String.valueOf(itemId));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                for (BorrowingDetailActivity.BorrowedItem item : returnItems) {
                    if (item.getReturnPhoto() != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        item.getReturnPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        params.put("return_photo_" + item.getItemId(), new DataPart("return_photo_" + item.getItemId() + ".jpg", imageBytes, "image/jpeg"));
                    }
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(ReturnFormActivity.this).getToken());
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
    }

    // Custom MultipartRequest class for file uploads
    public static class MultipartRequest extends Request<String> {

        private final Response.Listener<String> mListener;
        private final Map<String, String> mParams;
        private final Map<String, DataPart> mByteData;
        private final Map<String, String> mHeaders;

        public MultipartRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            mListener = listener;
            mParams = new HashMap<>();
            mByteData = new HashMap<>();
            mHeaders = new HashMap<>();
        }

        @Override
        protected Map<String, String> getParams() {
            return mParams;
        }

        protected Map<String, DataPart> getByteData() {
            return mByteData;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return mHeaders;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data, "UTF-8");
                return Response.success(jsonString, getCacheEntry());
            } catch (Exception e) {
                return Response.error(new VolleyError(e));
            }
        }

        @Override
        protected void deliverResponse(String response) {
            mListener.onResponse(response);
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; boundary=" + BOUNDARY;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                // Add text parameters
                for (Map.Entry<String, String> entry : getParams().entrySet()) {
                    buildTextPart(bos, entry.getKey(), entry.getValue());
                }

                // Add file parameters
                for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                    buildDataPart(bos, entry.getValue(), entry.getKey());
                }

                // Add closing boundary
                bos.write(("--" + BOUNDARY + "--\r\n").getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bos.toByteArray();
        }

        private void buildTextPart(ByteArrayOutputStream bos, String key, String value) throws IOException {
            bos.write(("--" + BOUNDARY + "\r\n").getBytes());
            bos.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
            bos.write(("Content-Type: text/plain\r\n\r\n").getBytes());
            bos.write((value + "\r\n").getBytes());
        }

        private void buildDataPart(ByteArrayOutputStream bos, DataPart dataPart, String inputName) throws IOException {
            bos.write(("--" + BOUNDARY + "\r\n").getBytes());
            bos.write(("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataPart.getFileName() + "\"\r\n").getBytes());
            bos.write(("Content-Type: " + dataPart.getType() + "\r\n\r\n").getBytes());
            bos.write(dataPart.getContent());
            bos.write(("\r\n").getBytes());
        }

        private static final String BOUNDARY = "apiclient-" + System.currentTimeMillis();

        public static class DataPart {
            private String fileName;
            private byte[] content;
            private String type;

            public DataPart() {
            }

            public DataPart(String name, byte[] data) {
                fileName = name;
                content = data;
            }

            public DataPart(String name, byte[] data, String mimeType) {
                fileName = name;
                content = data;
                type = mimeType;
            }

            public String getFileName() {
                return fileName;
            }

            public byte[] getContent() {
                return content;
            }

            public String getType() {
                return type;
            }
        }
    }
}
