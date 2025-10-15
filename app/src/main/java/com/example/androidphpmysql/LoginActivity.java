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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIdentifier, editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Cek jika user sudah login, redirect ke dashboard sesuai role
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            redirectBasedOnRole();
            return;
        }

        initializeViews();
        setupClickListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        editTextIdentifier = findViewById(R.id.editTextIdentifier);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
    }

    private void redirectBasedOnRole() {
        finish();
        String role = SharedPrefManager.getInstance(this).getUserRole();
        Intent intent;

        if ("students".equals(role)) {
            intent = new Intent(this, StudentDashboardActivity.class);
        } else if ("admin".equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, ProfileActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private void loginUser() {
        final String email = editTextIdentifier.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // Validasi input
        if (!validateInputs(email, password)) {
            return;
        }

        progressDialog.show();
        Log.d("LoginActivity", "Memulai login dengan email: " + email);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("LoginActivity", "Response login: " + response);
                        handleLoginResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        handleLoginError(error);
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                Log.d("LoginActivity", "Sending params - email: " + email + ", password length: " + password.length());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Set timeout policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            editTextIdentifier.setError("Please enter email");
            editTextIdentifier.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return false;
        }

        // Basic email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextIdentifier.setError("Please enter a valid email");
            editTextIdentifier.requestFocus();
            return false;
        }

        return true;
    }

    private void handleLoginResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean success = obj.getBoolean("success");
            String message = obj.getString("message");

            if (success) {
                JSONObject userJson = obj.getJSONObject("data");
                handleSuccessfulLogin(userJson, obj, message);
            } else {
                handleFailedLogin(message);
            }
        } catch (JSONException e) {
            handleJsonParsingError(e);
        }
    }

    private void handleSuccessfulLogin(JSONObject userJson, JSONObject responseObj, String message) throws JSONException {
        // Simpan data user ke SharedPreferences
        SharedPrefManager.getInstance(getApplicationContext())
                .userLogin(
                        userJson.getInt("id"),
                        userJson.getString("name"),
                        userJson.getString("email"),
                        userJson.getString("role")
                );

        // Save token
        String token = responseObj.getString("token");
        SharedPrefManager.getInstance(getApplicationContext()).saveToken(token);

        // Save user class if student
        saveUserClass(userJson);

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        Log.d("LoginActivity", "Login berhasil: " + message);

        // Redirect based on role
        redirectAfterLogin(userJson.getString("role"));
    }

    private void saveUserClass(JSONObject userJson) {
        String userClass = "";
        try {
            if (userJson.has("student") && !userJson.isNull("student")) {
                JSONObject student = userJson.getJSONObject("student");
                if (student.has("school_class") && !student.isNull("school_class")) {
                    JSONObject schoolClass = student.getJSONObject("school_class");
                    userClass = schoolClass.optString("name", "");
                }
            }
        } catch (JSONException e) {
            Log.e("LoginActivity", "Error parsing student class: " + e.getMessage());
        }
        SharedPrefManager.getInstance(getApplicationContext()).saveUserClass(userClass);
    }

    private void redirectAfterLogin(String role) {
        Intent intent;
        if ("students".equals(role)) {
            intent = new Intent(getApplicationContext(), StudentDashboardActivity.class);
        } else if ("admin".equals(role)) {
            intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), ProfileActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleFailedLogin(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.e("LoginActivity", "Login gagal: " + message);

        // Clear password field on failed login
        editTextPassword.setText("");
        editTextPassword.requestFocus();
    }

    private void handleJsonParsingError(JSONException e) {
        e.printStackTrace();
        String errorMessage = "Error parsing response: " + e.getMessage();
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        Log.e("LoginActivity", "Error parsing JSON: " + e.getMessage());
    }

    private void handleLoginError(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        String message = "Unknown error occurred.";

        if (networkResponse != null) {
            Log.e("LoginActivity", "Status Code: " + networkResponse.statusCode);
            message = handleNetworkError(networkResponse);
        } else {
            message = handleVolleyError(error);
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.e("LoginActivity", "Error response: " + message);

        // Log detailed error for debugging
        logDetailedError(error);
    }

    private String handleNetworkError(NetworkResponse networkResponse) {
        switch (networkResponse.statusCode) {
            case 401:
                return "Email atau password salah. Silakan coba lagi.";

            case 422:
                return handleValidationError(networkResponse);

            case 404:
                return "Endpoint tidak ditemukan. Periksa URL API.";

            case 500:
                return "Server error. Silakan coba lagi nanti.";

            case 403:
                return "Akses ditolak. Akun mungkin tidak memiliki izin.";

            default:
                return "Error: " + networkResponse.statusCode + ". Silakan coba lagi.";
        }
    }

    private String handleValidationError(NetworkResponse networkResponse) {
        try {
            String responseBody = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
            JSONObject errorObj = new JSONObject(responseBody);

            if (errorObj.has("message")) {
                return errorObj.getString("message");
            } else if (errorObj.has("errors")) {
                JSONObject errors = errorObj.getJSONObject("errors");
                if (errors.has("email")) {
                    return errors.getJSONArray("email").getString(0);
                } else if (errors.has("password")) {
                    return errors.getJSONArray("password").getString(0);
                }
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error parsing validation error: " + e.getMessage());
        }
        return "Data yang dimasukkan tidak valid.";
    }

    private String handleVolleyError(VolleyError error) {
        if (error instanceof com.android.volley.TimeoutError) {
            return "Koneksi timeout. Periksa koneksi internet atau server.";
        } else if (error instanceof com.android.volley.NoConnectionError) {
            return "Tidak ada koneksi internet. Periksa koneksi Anda.";
        } else if (error instanceof com.android.volley.AuthFailureError) {
            return "Authentication failed. Please check your credentials.";
        } else if (error.getMessage() != null) {
            return error.getMessage();
        } else {
            return "Unknown network error occurred.";
        }
    }

    private void logDetailedError(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            try {
                String errorResponse = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                Log.e("LoginActivity", "Error response body: " + errorResponse);
            } catch (Exception e) {
                Log.e("LoginActivity", "Error parsing error response: " + e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            loginUser();
        } else if (view == textViewForgotPassword) {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        // Dismiss progress dialog to prevent window leak
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}