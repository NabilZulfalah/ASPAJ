package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.androidphpmysql.Constants;
import com.example.androidphpmysql.SharedPrefManager;
import com.example.androidphpmysql.VolleySingleton;
import java.io.ByteArrayOutputStream;
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
    private int borrowingId;

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

        // Get borrowing ID from intent
        borrowingId = getIntent().getIntExtra("borrowing_id", -1);

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
        if (borrowingId == -1) {
            Toast.makeText(this, "Invalid borrowing ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Submitting return...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.POST_UPDATE_BORROWING_STATUS_URL,
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
                params.put("borrowing_id", String.valueOf(borrowingId));
                params.put("status", "returned");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(ReturnFormActivity.this).getToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
