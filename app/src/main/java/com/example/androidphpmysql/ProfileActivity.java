package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AssetAdapter.QuantityChangeListener {

    private TextView textViewUsername, textViewEmail;
    private Button buttonAssetList, buttonUserManagement, buttonKelasManagement;
    private RecyclerView recyclerView;
    private AssetAdapter adapter;
    private List<Asset> assetList;

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;

    private RecyclerView recyclerViewKelas;
    private SchoolClassAdapter kelasAdapter;
    private List<Kelas> kelasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
        }

        // Cek login
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Bind UI
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonAssetList = findViewById(R.id.buttonAssetList);
        buttonUserManagement = findViewById(R.id.buttonUserManagement);
        buttonKelasManagement = findViewById(R.id.buttonKelasManagement);

        recyclerView = findViewById(R.id.recyclerViewAssets);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewKelas = findViewById(R.id.recyclerViewKelas);
        recyclerViewKelas.setHasFixedSize(true);
        recyclerViewKelas.setLayoutManager(new LinearLayoutManager(this));

        assetList = new ArrayList<>();
        userList = new ArrayList<>();
        kelasList = new ArrayList<>();

        loadAssets();
        loadUsers();
        loadKelas();

        // Set data user
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        textViewEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());

        // Set listeners
        buttonAssetList.setOnClickListener(this);
        buttonUserManagement.setOnClickListener(this);
        buttonKelasManagement.setOnClickListener(this);

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuLogout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (id == R.id.menuSettings) {
            Toast.makeText(this, "You clicked settings", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonAssetList) {
            startActivity(new Intent(this, AssetListActivity.class));
        } else if (view.getId() == R.id.buttonUserManagement) {
            startActivity(new Intent(this, UserManagementActivity.class));
        } else if (view.getId() == R.id.buttonKelasManagement) {
            startActivity(new Intent(this, KelasManagementActivity.class));
        }
    }

    public void loadAssets() {
        assetList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_ASSETS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("success")) {
                        JSONArray assets = obj.getJSONArray("data");

                        for (int i = 0; i < assets.length(); i++) {
                            JSONObject assetObject = assets.getJSONObject(i);

                            int id = assetObject.getInt("id");
                            String namaBarang = assetObject.optString("name", "");
                            String kodeBarang = assetObject.optString("code", "");
                            String jumlahStok = String.valueOf(assetObject.optInt("stock", 0));
                            String lokasiBarang = assetObject.optString("lokasi", "");
                            String jurusanBarang = assetObject.optString("jurusan", "");
                            String merk = assetObject.optString("merk", "");
                            double hargaSatuan = assetObject.isNull("harga_satuan") ? 0.0 : assetObject.getDouble("harga_satuan");
                            String sumber = assetObject.optString("sumber", "");
                            String tahun = assetObject.isNull("tahun") ? "" : String.valueOf(assetObject.getInt("tahun"));
                            String deskripsi = assetObject.optString("deskripsi", "");

                            Asset asset = new Asset(id, namaBarang, kodeBarang, jumlahStok, lokasiBarang, jurusanBarang, merk, hargaSatuan, sumber, tahun, deskripsi);

                            assetList.add(asset);
                        }

                        adapter = new AssetAdapter(ProfileActivity.this, assetList, ProfileActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Network error occurred";
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void loadUsers() {
        userList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_USERS, new Response.Listener<String>() {
            @Override
                    public void onResponse(String response) {
                        try {
                            // Remove any leading characters before JSON object
                            int jsonStart = response.indexOf("{");
                            if (jsonStart > 0) {
                                response = response.substring(jsonStart);
                            }
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success")) {
                                JSONArray users = obj.getJSONArray("data");

                        for (int i = 0; i < users.length(); i++) {
                            JSONObject userObject = users.getJSONObject(i);

                            int id = userObject.optInt("id", 0);
                            String name = userObject.optString("name", "");
                            String email = userObject.optString("email", "");
                            String role = userObject.optString("role", "");
                            String approvalStatus = userObject.optString("approval_status", "");

                            User user = new User(id, name, email, role, approvalStatus);

                            userList.add(user);
                        }

                        userAdapter = new UserAdapter(ProfileActivity.this, userList, null);
                        recyclerViewUsers.setAdapter(userAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing user data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Network error occurred";
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void loadKelas() {
        kelasList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_KELAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("success")) {
                        JSONArray kelasArray = obj.getJSONArray("data");

                        for (int i = 0; i < kelasArray.length(); i++) {
                            JSONObject kelasObject = kelasArray.getJSONObject(i);

                            String id = String.valueOf(kelasObject.optInt("id", 0));
                            String name = kelasObject.optString("name", "");
                            String level = kelasObject.optString("level", "");
                            String programStudy = kelasObject.optString("program_study", "");
                            String capacity = kelasObject.optString("capacity", "");
                            String description = kelasObject.optString("description", "");

                            Kelas kelas = new Kelas(id, name, level, programStudy, capacity, description, "", "");

                            kelasList.add(kelas);
                        }

                        List<SchoolClass> schoolClassList = new ArrayList<>();
                        for (Kelas k : kelasList) {
                            int capacity = 0;
                            try {
                                capacity = Integer.parseInt(k.getCapacity());
                            } catch (NumberFormatException e) {
                                capacity = 0;
                            }
                            SchoolClass sc = new SchoolClass(Integer.parseInt(k.getId()), k.getName(), k.getLevel(), k.getProgramStudy(), capacity, k.getDescription());
                            schoolClassList.add(sc);
                        }

                        kelasAdapter = new SchoolClassAdapter(ProfileActivity.this, schoolClassList, null);
                        recyclerViewKelas.setAdapter(kelasAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing kelas data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = error.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Network error occurred";
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onQuantityChange(int assetId, int quantity) {
        // Profile activity does not handle quantity changes
    }
}
