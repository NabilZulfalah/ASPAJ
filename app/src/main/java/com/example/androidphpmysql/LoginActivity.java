package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIdentifier, editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            String role = SharedPrefManager.getInstance(this).getUserRole();
            if ("students".equals(role)) {
                startActivity(new Intent(this, StudentDashboardActivity.class));
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return;
        }

        editTextIdentifier = (EditText) findViewById(R.id.editTextIdentifier);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        buttonLogin.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Method untuk melakukan login user menggunakan API Laravel.
     * Mengirim POST request ke /api/login dengan parameter email dan password.
     * Parsing response JSON Laravel: {success: boolean, message: string, data: object}
     */
    private void loginUser() {
        // Ambil input email dan password dari EditText
        final String email = editTextIdentifier.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // Tampilkan progress dialog
        progressDialog.show();

        // Log untuk menandai mulai proses login
        Log.d("LoginActivity", "Memulai login dengan email: " + email);

        // Buat StringRequest untuk POST request ke endpoint login Laravel
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, // Method HTTP POST
                Constants.LOGIN_URL, // URL endpoint login dari Constants
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Sembunyikan progress dialog
                        progressDialog.dismiss();

                        // Log response untuk debugging
                        Log.d("LoginActivity", "Response login: " + response);

                        try {
                            // Parsing response JSON Laravel
                            JSONObject obj = new JSONObject(response);
                            boolean success = obj.getBoolean("success"); // Cek status login
                            String message = obj.getString("message"); // Pesan dari server

                            if (success) {
                                // Jika login berhasil, ambil data user dari response
                                JSONObject userJson = obj.getJSONObject("data");

                                // Simpan data user ke SharedPreferences menggunakan SharedPrefManager
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                userJson.getInt("id"),
                                                userJson.getString("name"),
                                                userJson.getString("email"),
                                                userJson.getString("role")
                                        );

                                // Save token to SharedPrefManager
                                String token = obj.getString("token");
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .saveToken(token);

                                // Tampilkan pesan sukses di Toast
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                // Log sukses login
                                Log.d("LoginActivity", "Login berhasil: " + message);

                                // Pindah ke activity berdasarkan role
                                String role = userJson.getString("role");
                                Intent intent;
                                if ("students".equals(role)) {
                                    intent = new Intent(getApplicationContext(), StudentDashboardActivity.class);
                                } else {
                                    intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            } else {
                                // Jika login gagal, tampilkan pesan error di Toast
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "Login gagal: " + message);
                            }
                        } catch (JSONException e) {
                            // Tangani error parsing JSON
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Sembunyikan progress dialog
                        progressDialog.dismiss();

                        // Tentukan pesan error berdasarkan jenis error
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

                        // Tampilkan pesan error di Toast
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Error response: " + message);
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Siapkan parameter POST: email dan password
                Map<String, String> params = new HashMap<>();
                params.put("email", editTextIdentifier.getText().toString().trim()); // Parameter email
                params.put("password", editTextPassword.getText().toString().trim()); // Parameter password
                return params;
            }
        };

        // Tambahkan request ke RequestQueue menggunakan VolleySingleton
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            loginUser();
        }
    }

    // Add method to save token in SharedPrefManager
    private void saveToken(String token) {
        SharedPrefManager.getInstance(this).saveToken(token);
    }
}
