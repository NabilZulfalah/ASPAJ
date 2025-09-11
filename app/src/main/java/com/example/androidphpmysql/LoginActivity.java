package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
    private TextView textViewRegister; // Tambahkan TextView untuk register
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextIdentifier = findViewById(R.id.editTextIdentifier);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        // Set click listeners
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

        // Validasi input
        if (identifier.isEmpty()) {
            editTextIdentifier.setError("Please enter email or username");
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
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.getBoolean("error")) {
                                JSONObject userJson = obj.getJSONObject("user");

                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                userJson.getInt("id"),
                                                userJson.getString("name"),
                                                userJson.getString("email")
                                        );

                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                finish();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        String message;
                        if (error instanceof com.android.volley.TimeoutError) {
                            message = "Connection timed out. Check server or internet.";
                        } else if (error instanceof com.android.volley.NoConnectionError) {
                            message = "No connection. Please check your internet.";
                        } else if (error.getMessage() != null) {
                            message = error.getMessage();
                        } else {
                            message = "Unknown error occurred.";
                        }

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("identifier", editTextIdentifier.getText().toString().trim());
                params.put("password", editTextPassword.getText().toString().trim());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.buttonLogin) {
            loginUser();
        } else if (viewId == R.id.textViewLogin) {
            // Pindah ke MainActivity2 (Register)
            startActivity(new Intent(LoginActivity.this, MainActivity2.class));
            finish();
        }
    }
}