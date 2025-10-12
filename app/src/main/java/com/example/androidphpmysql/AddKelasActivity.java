package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.DefaultRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddKelasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddKelasActivity";

    private EditText editTextName, editTextCapacity, editTextDescription;
    private Spinner spinnerLevel, spinnerProgramStudy;
    private Button buttonSimpan, buttonKembali;
    private ProgressDialog progressDialog;
    private int kelasId = -1; // -1 for new kelas, otherwise edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kelas);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(kelasId == -1 ? "Tambah Kelas" : "Edit Kelas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        editTextName = findViewById(R.id.editTextName);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        spinnerProgramStudy = findViewById(R.id.spinnerProgramStudy);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSimpan = findViewById(R.id.buttonSimpan);
        buttonKembali = findViewById(R.id.buttonKembali);

        // Set spinner adapters
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.level_array, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this,
                R.array.jurusan_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgramStudy.setAdapter(programAdapter);

        buttonSimpan.setOnClickListener(this);
        buttonKembali.setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if editing
        if (getIntent().hasExtra("kelas_id")) {
            kelasId = getIntent().getIntExtra("kelas_id", -1);
            editTextName.setText(getIntent().getStringExtra("kelas_name"));
            String level = getIntent().getStringExtra("kelas_level");
            if (level != null) {
                int spinnerPosition = levelAdapter.getPosition(level);
                spinnerLevel.setSelection(spinnerPosition);
            }
            String program = getIntent().getStringExtra("kelas_program_study");
            if (program != null) {
                int spinnerPosition = programAdapter.getPosition(program);
                spinnerProgramStudy.setSelection(spinnerPosition);
            }
            editTextCapacity.setText(String.valueOf(getIntent().getIntExtra("kelas_capacity", 0)));
            editTextDescription.setText(getIntent().getStringExtra("kelas_description"));
        }
    }

    private void saveKelas() {
        String name = editTextName.getText().toString().trim();
        String level = spinnerLevel.getSelectedItem() != null ? spinnerLevel.getSelectedItem().toString() : "";
        String programStudy = spinnerProgramStudy.getSelectedItem() != null ? spinnerProgramStudy.getSelectedItem().toString() : "";
        String capacityStr = editTextCapacity.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty() || level.isEmpty() || programStudy.isEmpty() || capacityStr.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Kapasitas harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Saving kelas...");
        progressDialog.show();

        String url = (kelasId == -1) ? Constants.URL_ADD_KELAS : Constants.URL_UPDATE_KELAS;
        int method = Request.Method.POST;

        StringRequest stringRequest = new StringRequest(
                method,
                url,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "SAVE_RESPONSE: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.optString("message", "Unknown error");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        if (jsonObject.getBoolean("success")) {
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMessage = "Error saving kelas";
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        try {
                            String responseBody = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                            JSONObject data = new JSONObject(responseBody);
                            if (data.has("message")) {
                                errorMessage = data.getString("message");
                                Log.e(TAG, "Laravel Error: " + errorMessage, error);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response", e);
                        }
                    }
                    Log.e(TAG, "Volley error", error);
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("level", level);
                params.put("program_study", programStudy);
                params.put("capacity", String.valueOf(capacity));
                params.put("description", description);
                if (kelasId != -1) {
                    params.put("id", String.valueOf(kelasId));
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSimpan) {
            saveKelas();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
