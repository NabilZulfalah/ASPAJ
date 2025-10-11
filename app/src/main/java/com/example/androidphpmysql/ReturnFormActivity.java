package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.androidphpmysql.Constants;
import com.example.androidphpmysql.SharedPrefManager;
import com.example.androidphpmysql.VolleySingleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReturnFormActivity extends AppCompatActivity {

    private Spinner spinnerCondition;
    private ImageView imageViewPhoto;
    private Button buttonUploadPhoto, buttonSubmitReturn;
    private Bitmap selectedImage;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String borrowingId;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_form);

        spinnerCondition = findViewById(R.id.spinnerCondition);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto);
        buttonSubmitReturn = findViewById(R.id.buttonSubmitReturn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Get borrowing ID and item ID from intent
        borrowingId = getIntent().getStringExtra("borrowing_id");
        itemId = getIntent().getIntExtra("item_id", -1);

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.condition_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(adapter);

        // Setup image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            try {
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                imageViewPhoto.setImageBitmap(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ReturnFormActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        buttonUploadPhoto.setOnClickListener(v -> openImagePicker());

        buttonSubmitReturn.setOnClickListener(v -> submitReturn());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void submitReturn() {
        if (borrowingId == null || itemId == -1) {
            Toast.makeText(this, "Invalid borrowing or item ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String condition = spinnerCondition.getSelectedItem().toString();
        String description = ""; // Optional description

        progressDialog.setMessage("Submitting return...");
        progressDialog.show();

        String url = Constants.POST_RETURN_BORROWING_URL.replace("{id}", String.valueOf(borrowingId));

        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(ReturnFormActivity.this, "Return submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(ReturnFormActivity.this, "Error submitting return: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_id", String.valueOf(itemId));
                params.put("condition", condition);
                params.put("description", description);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (selectedImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    params.put("photo", new DataPart("return_photo.jpg", imageBytes, "image/jpeg"));
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
