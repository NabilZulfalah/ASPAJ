package com.example.androidphpmysql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView textViewName, textViewRole, textViewClass;
    private ImageView imageViewProfilePhoto;
    private TextView textViewNameRight, textViewEmailRight, textViewRoleRight, textViewClassRight;
    private Button buttonChangePassword, buttonLogout, buttonSave;

    private Uri selectedImageUri;
    private String originalName, originalEmail, originalRole, originalClass;
    private String avatarUrl;
    private int userId;
    private int schoolClassId;

    // Drawer components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imageViewProfilePhoto.setImageURI(uri);
                    showConfirmationDialog(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Cek login
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Bind UI
        textViewName = findViewById(R.id.textViewName);
        textViewRole = findViewById(R.id.textViewRole);
        textViewClass = findViewById(R.id.textViewClass);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        textViewNameRight = findViewById(R.id.textViewNameRight);
        textViewEmailRight = findViewById(R.id.textViewEmailRight);
        textViewRoleRight = findViewById(R.id.textViewRoleRight);
        textViewClassRight = findViewById(R.id.textViewClassRight);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSave = findViewById(R.id.buttonSave);

        // Initialize drawer components
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set navigation listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set listeners
        buttonChangePassword.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        imageViewProfilePhoto.setOnClickListener(v -> showPhotoOptionsDialog());
        buttonSave.setOnClickListener(v -> uploadProfilePhoto());

        // Load user profile
        loadUserProfile();

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    private void loadUserProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success")) {
                                JSONObject user = obj.getJSONObject("data");

                                userId = user.optInt("id", 0);
                                originalName = user.optString("name", SharedPrefManager.getInstance(ProfileActivity.this).getUsername());
                                originalEmail = user.optString("email", SharedPrefManager.getInstance(ProfileActivity.this).getUserEmail());
                                originalRole = user.optString("role", SharedPrefManager.getInstance(ProfileActivity.this).getUserRole());
                                String className = "";
                                try {
                                    if (user.has("student") && !user.isNull("student")) {
                                        JSONObject student = user.getJSONObject("student");
                                        if (student.has("school_class") && !student.isNull("school_class")) {
                                            JSONObject schoolClass = student.getJSONObject("school_class");
                                            className = schoolClass.optString("name", "");
                                            schoolClassId = schoolClass.optInt("id", 0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                originalClass = className;
                                SharedPrefManager.getInstance(ProfileActivity.this).saveUserClass(className);
                                avatarUrl = user.optString("profile_picture", "");

                                // Update left panel
                                textViewName.setText(originalName);
                                textViewRole.setText(originalRole);
                                textViewClass.setText(originalClass);

                                // Update right panel
                                textViewNameRight.setText("Nama: " + originalName);
                                textViewEmailRight.setText("Email: " + originalEmail);
                                textViewRoleRight.setText("Role: " + originalRole);
                                textViewClassRight.setText("Kelas: " + originalClass);

                                // Load profile photo
                                Glide.with(ProfileActivity.this)
                                        .load(avatarUrl.isEmpty() ? R.drawable.ic_asset_placeholder : Constants.STORAGE_BASE + avatarUrl)
                                        .placeholder(R.drawable.ic_asset_placeholder)
                                        .circleCrop()
                                        .into(imageViewProfilePhoto);

                                selectedImageUri = null;

                            } else {
                                // Fallback to SharedPref
                                userId = SharedPrefManager.getInstance(ProfileActivity.this).getUserId();
                                originalName = SharedPrefManager.getInstance(ProfileActivity.this).getUsername();
                                originalRole = SharedPrefManager.getInstance(ProfileActivity.this).getUserRole();
                                originalEmail = SharedPrefManager.getInstance(ProfileActivity.this).getUserEmail();
                                originalClass = SharedPrefManager.getInstance(ProfileActivity.this).getUserClass();
                                if (originalClass == null) originalClass = "";
                                avatarUrl = "";

                                textViewName.setText(originalName);
                                textViewRole.setText(originalRole);
                                textViewClass.setText(originalClass);

                                textViewNameRight.setText("Nama: " + originalName);
                                textViewEmailRight.setText("Email: " + originalEmail);
                                textViewRoleRight.setText("Role: " + originalRole);
                                textViewClassRight.setText("Kelas: " + originalClass);

                        if (!isFinishing() && !isDestroyed()) {
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.ic_asset_placeholder)
                                    .placeholder(R.drawable.ic_asset_placeholder)
                                    .circleCrop()
                                    .into(imageViewProfilePhoto);
                        }

                                selectedImageUri = null;

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing profile data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            try {
                                String responseBody = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
                                JSONObject data = new JSONObject(responseBody);
                                if (data.has("message")) {
                                    String errorMessage = data.getString("message");
                                    Log.e("ProfileActivity", "Laravel Error: " + errorMessage, error);
                                }
                            } catch (Exception e) {
                                Log.e("ProfileActivity", "Error parsing error response", e);
                            }
                        }
                        Log.e("ProfileActivity", "Volley error", error);
                        Toast.makeText(getApplicationContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                        // Fallback
                        userId = SharedPrefManager.getInstance(ProfileActivity.this).getUserId();
                        originalName = SharedPrefManager.getInstance(ProfileActivity.this).getUsername();
                        originalRole = SharedPrefManager.getInstance(ProfileActivity.this).getUserRole();
                        originalEmail = SharedPrefManager.getInstance(ProfileActivity.this).getUserEmail();
                        originalClass = SharedPrefManager.getInstance(ProfileActivity.this).getUserClass();
                        if (originalClass == null) originalClass = "";
                        avatarUrl = "";

                        textViewName.setText(originalName);
                        textViewRole.setText(originalRole);
                        textViewClass.setText(originalClass);

                        textViewNameRight.setText("Nama: " + originalName);
                        textViewEmailRight.setText("Email: " + originalEmail);
                        textViewRoleRight.setText("Role: " + originalRole);
                        textViewClassRight.setText("Kelas: " + originalClass);

                        if (!isFinishing() && !isDestroyed()) {
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.ic_asset_placeholder)
                                    .placeholder(R.drawable.ic_asset_placeholder)
                                    .circleCrop()
                                    .into(imageViewProfilePhoto);
                        }

                        selectedImageUri = null;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(ProfileActivity.this).getToken());
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void uploadProfilePhoto() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == 0) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            Log.e("ProfileActivity", "User ID is 0, cannot upload");
            return;
        }

        Log.d("ProfileActivity", "Starting upload for userId: " + userId);

        Map<String, String> params = new HashMap<>();

        Map<String, File> files = new HashMap<>();
        if (selectedImageUri != null) {
            try {
                File photoFile = createTempFileFromUri(selectedImageUri);
                if (photoFile != null && photoFile.exists()) {
                    files.put("profile_picture", photoFile);
                    Log.d("ProfileActivity", "File created: " + photoFile.getAbsolutePath() + ", size: " + photoFile.length());
                } else {
                    Log.e("ProfileActivity", "Failed to create temp file");
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Log.e("ProfileActivity", "Exception creating file: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Map<String, String> headers = new HashMap<>();
        String token = SharedPrefManager.getInstance(this).getToken();
        headers.put("Authorization", "Bearer " + token);
        Log.d("ProfileActivity", "Token: " + token);

        String url = Constants.URL_UPDATE_PROFILE;
        Log.d("ProfileActivity", "URL: " + url);

        MultipartRequest multipartRequest = new MultipartRequest(
                Request.Method.POST,
                url,
                params,
                files,
                headers,
                response -> {
                    Log.d("ProfileActivity", "Response: " + response);
                    try {
                        JSONObject obj = response;
                        if (obj.getBoolean("success")) {
                            Toast toast = Toast.makeText(ProfileActivity.this, "Profile photo updated successfully", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
                            toast.show();
                            loadUserProfile(); // Reload to update UI
                        } else {
                            Toast.makeText(ProfileActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ProfileActivity", "JSON parse error: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ProfileActivity", "Volley error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("ProfileActivity", "Status code: " + error.networkResponse.statusCode);
                        Log.e("ProfileActivity", "Response data: " + new String(error.networkResponse.data));
                        try {
                            String responseBody = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, "utf-8"));
                            JSONObject data = new JSONObject(responseBody);
                            if (data.has("message")) {
                                String errorMessage = data.getString("message");
                                Log.e("ProfileActivity", "Laravel Error: " + errorMessage, error);
                            }
                        } catch (Exception e) {
                            Log.e("ProfileActivity", "Error parsing error response", e);
                        }
                    }
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Toast.makeText(ProfileActivity.this, "Error updating profile photo: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
        );

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
    }

    private void showConfirmationDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Foto Profil");
        builder.setMessage("Apakah Anda ingin menggunakan foto ini sebagai foto profil?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            selectedImageUri = imageUri;
            imageViewProfilePhoto.setImageURI(imageUri);
            Toast.makeText(this, "Foto dipilih. Klik Simpan untuk upload.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            selectedImageUri = null;
            imageViewProfilePhoto.setImageResource(R.drawable.ic_asset_placeholder);
            dialog.dismiss();
        });
        builder.show();
    }

    private File createTempFileFromUri(Uri uri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("ProfileActivity", "Failed to open input stream for URI: " + uri);
                return null;
            }
            File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            return tempFile;
        } catch (Exception e) {
            Log.e("ProfileActivity", "Exception creating temp file from URI: " + uri, e);
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    Log.e("ProfileActivity", "Failed to close output stream", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("ProfileActivity", "Failed to close input stream", e);
                }
            }
        }
    }

    private void showPhotoOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Opsi Foto Profil");
        String[] options = {"Tambah Foto", "Hapus Foto", "Batal"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Tambah Foto
                    pickImage();
                    break;
                case 1: // Hapus Foto
                    selectedImageUri = null;
                    imageViewProfilePhoto.setImageResource(R.drawable.ic_asset_placeholder);
                    break;
                case 2: // Batal
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonChangePassword) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (view.getId() == R.id.buttonLogout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@androidx.annotation.NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        String role = SharedPrefManager.getInstance(this).getUserRole();

        if (id == R.id.nav_dashboard) {
            if ("student".equals(role)) {
                startActivity(new Intent(this, StudentDashboardActivity.class));
            } else if ("officer".equals(role)) {
                startActivity(new Intent(this, OfficerDashboardActivity.class));
            }
        } else if (id == R.id.nav_borrow_assets) {
            if ("student".equals(role)) {
                startActivity(new Intent(this, SelectJurusanActivity.class));
            } else if ("officer".equals(role)) {
                // Officers might not borrow, or go to asset list
                startActivity(new Intent(this, AssetListActivity.class));
            }
        } else if (id == R.id.nav_my_borrowings) {
            startActivity(new Intent(this, BorrowingStatusActivity.class));
        } else if (id == R.id.nav_profile) {
            // Already on profile, do nothing
        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
