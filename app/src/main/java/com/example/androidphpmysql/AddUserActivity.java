package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private Spinner spinnerRole, spinnerStatus;
    private Button buttonSave, buttonCancel;

    private User userToEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        editTextName = findViewById(R.id.edittext_name);
        editTextEmail = findViewById(R.id.edittext_email);
        spinnerRole = findViewById(R.id.spinner_role);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_filter_options, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Check if editing existing user
        userToEdit = (User) getIntent().getSerializableExtra("user");
        if (userToEdit != null) {
            editTextName.setText(userToEdit.getName());
            editTextEmail.setText(userToEdit.getEmail());
            setSpinnerSelection(spinnerRole, userToEdit.getRole());
            setSpinnerSelection(spinnerStatus, userToEdit.getStatus());
            editTextEmail.setEnabled(false); // Email as unique ID, disable editing
        }

        buttonSave.setOnClickListener(v -> saveUser());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nama harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = (userToEdit == null) ? Constants.URL_ADD_USER : Constants.URL_UPDATE_USER;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Add logging to see what we received
                        android.util.Log.d("ServerResponse", "Response: " + response);

                        try {
                            // Trim any whitespace that might interfere
                            response = response.trim();

                            // Check if response looks like JSON
                            if (!response.startsWith("{")) {
                                Toast.makeText(AddUserActivity.this,
                                        "Server error: Invalid response format",
                                        Toast.LENGTH_LONG).show();
                                android.util.Log.e("ServerResponse", "Non-JSON response: " + response);
                                return;
                            }

                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.getBoolean("error")) {
                                User user = new User(name, email, role, status);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("user", user);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                                Toast.makeText(AddUserActivity.this,
                                        "User saved successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddUserActivity.this,
                                        "Error: " + jsonResponse.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddUserActivity.this,
                                    "Error: Server returned invalid data",
                                    Toast.LENGTH_LONG).show();
                            android.util.Log.e("JSONError", "Failed to parse: " + response, e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Network error";
                        if (error.networkResponse != null) {
                            errorMsg += " (Code: " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(AddUserActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        android.util.Log.e("NetworkError", "Error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("role", role);
                if (userToEdit != null) {
                    params.put("status", status);
                }
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
