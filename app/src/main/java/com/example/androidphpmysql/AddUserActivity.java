package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.DefaultRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddUserActivity";

    private EditText editTextName, editTextEmail;
    private Spinner spinnerRole, spinnerApprovalStatus, spinnerJurusan;
    private Button buttonSimpan, buttonKembali;
    private ProgressDialog progressDialog;
    private int userId = -1; // -1 for new user, otherwise edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userId == -1 ? "Tambah User" : "Edit User");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerRole = findViewById(R.id.spinnerRole);
        spinnerApprovalStatus = findViewById(R.id.spinnerApprovalStatus);
        spinnerJurusan = findViewById(R.id.spinnerJurusan);
        buttonSimpan = findViewById(R.id.buttonSimpan);
        buttonKembali = findViewById(R.id.buttonKembali);

        // Set spinner adapters
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        ArrayAdapter<CharSequence> approvalAdapter = ArrayAdapter.createFromResource(this,
                R.array.approval_array, android.R.layout.simple_spinner_item);
        approvalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerApprovalStatus.setAdapter(approvalAdapter);

        ArrayAdapter<CharSequence> jurusanAdapter = ArrayAdapter.createFromResource(this,
                R.array.jurusan_array, android.R.layout.simple_spinner_item);
        jurusanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJurusan.setAdapter(jurusanAdapter);

        buttonSimpan.setOnClickListener(this);
        buttonKembali.setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if editing
        if (getIntent().hasExtra("user_id")) {
            userId = getIntent().getIntExtra("user_id", -1);
            editTextName.setText(getIntent().getStringExtra("user_name"));
            editTextEmail.setText(getIntent().getStringExtra("user_email"));
            String role = getIntent().getStringExtra("user_role");
            if (role != null) {
                int spinnerPosition = roleAdapter.getPosition(role);
                spinnerRole.setSelection(spinnerPosition);
            }
            String approval = getIntent().getStringExtra("user_approval_status");
            if (approval != null) {
                int spinnerPosition = approvalAdapter.getPosition(approval);
                spinnerApprovalStatus.setSelection(spinnerPosition);
            }
            String jurusan = getIntent().getStringExtra("user_jurusan");
            if (jurusan != null) {
                int spinnerPosition = jurusanAdapter.getPosition(jurusan);
                spinnerJurusan.setSelection(spinnerPosition);
            }
        }
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String role = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "";
        String approvalStatus = spinnerApprovalStatus.getSelectedItem() != null ? spinnerApprovalStatus.getSelectedItem().toString() : "";
        String jurusan = spinnerJurusan.getSelectedItem() != null ? spinnerJurusan.getSelectedItem().toString() : "";

        if (name.isEmpty() || email.isEmpty() || role.isEmpty() || approvalStatus.isEmpty() || jurusan.isEmpty() || jurusan.equals("Semua Program Studi")) {
            Toast.makeText(this, "Semua field harus diisi dengan benar", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Saving user...");
        progressDialog.show();

        String url = (userId == -1) ? Constants.URL_ADD_USER : Constants.URL_UPDATE_USER;
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
                            User user = new User(userId != -1 ? userId : 0, name, email, role, approvalStatus, jurusan);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("user", user);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Volley error", error);
                    Toast.makeText(getApplicationContext(), "Error saving user", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("role", role);
                params.put("approval_status", approvalStatus);
                params.put("jurusan", jurusan);
                if (userId != -1) {
                    params.put("id", String.valueOf(userId));
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
            saveUser();
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
