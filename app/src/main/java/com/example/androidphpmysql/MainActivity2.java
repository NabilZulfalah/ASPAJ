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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";

    private EditText editTextUsername, editTextUserPassword, editTextEmail;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Pastikan layout yang benar dipakai
        setContentView(R.layout.activity_main2);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        // Binding view
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextUserPassword = findViewById(R.id.editTextUserPassword);
        textViewLogin = findViewById(R.id.textViewLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Debug untuk cek null view
        Log.d(TAG, "editTextEmail = " + editTextEmail);
        Log.d(TAG, "editTextUsername = " + editTextUsername);
        Log.d(TAG, "editTextUserPassword = " + editTextUserPassword);
        Log.d(TAG, "textViewLogin = " + textViewLogin);
        Log.d(TAG, "buttonRegister = " + buttonRegister);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (buttonRegister != null) buttonRegister.setOnClickListener(this);
        if (textViewLogin != null) textViewLogin.setOnClickListener(this);
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextUserPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        Log.d(TAG, "registerUser() called with: username=" + username + ", email=" + email);

        // Validasi sederhana
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Validation failed: ada field kosong");
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "REGISTER_RESPONSE: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error", e);
                            Toast.makeText(getApplicationContext(),
                                    "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Volley error", error);
                        if (error.getMessage() != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Unknown error occurred",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", username);
                params.put("password", password);
                params.put("email", email);
                Log.d(TAG, "Params: " + params.toString());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: viewId=" + view.getId());
        if (view.getId() == R.id.buttonRegister) {
            registerUser();
        } else if (view.getId() == R.id.textViewLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}
