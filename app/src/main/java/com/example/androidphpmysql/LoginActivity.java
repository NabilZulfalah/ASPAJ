package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIdentifier, editTextPassword;
    private Button btnLogin;
    private TextView textViewRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextIdentifier = findViewById(R.id.editTextIdentifier);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.txRegisterHere);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser() {
        final String identifier = editTextIdentifier.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        Log.d("LoginDebug", "Attempting login with - Identifier: " + identifier);

        if (identifier.isEmpty()) {
            editTextIdentifier.setError("Please enter username or email");
            editTextIdentifier.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("LoginDebug", "Server Response: " + response);

                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.has("error")) {
                                Toast.makeText(getApplicationContext(), "Invalid server response", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (!obj.getBoolean("error")) {
                                JSONObject userJson = obj.getJSONObject("user");

                                // Handle null values safely
                                String profilePicture = userJson.isNull("profile_picture") || userJson.getString("profile_picture").isEmpty() ?
                                        null : userJson.getString("profile_picture");
                                String nls = userJson.isNull("nls") || userJson.getString("nls").isEmpty() ?
                                        null : userJson.getString("nls");
                                String role = userJson.isNull("role") ? "user" : userJson.getString("role");

                                // Simpan data user ke SharedPreferences
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                userJson.getInt("id"),
                                                userJson.getString("name"),
                                                userJson.getString("email"),
                                                profilePicture,
                                                nls,
                                                role
                                        );

                                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                finish();
                            } else {
                                String errorMessage = obj.getString("message");
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                                Log.e("LoginError", "Server error: " + errorMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Login error: Invalid response format", Toast.LENGTH_SHORT).show();
                            Log.e("LoginError", "JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("LoginError", "Volley error: " + error.toString());

                        String message = "Network error: ";
                        if (error.networkResponse != null) {
                            message += "Status " + error.networkResponse.statusCode;
                            Log.e("LoginError", "Status code: " + error.networkResponse.statusCode);
                        } else if (error.getMessage() != null) {
                            message += error.getMessage();
                        } else {
                            message += "Unknown error occurred";
                        }

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("identifier", identifier);
                params.put("password", password);
                Log.d("LoginDebug", "Sending params: " + params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
        };

        // Set retry policy
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.buttonLogin) {
            loginUser();
        } else if (viewId == R.id.txRegisterHere) {
            startActivity(new Intent(LoginActivity.this, MainActivity2.class));
        }
    }
}