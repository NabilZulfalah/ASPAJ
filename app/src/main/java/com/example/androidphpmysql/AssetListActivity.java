package com.example.androidphpmysql;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetListActivity extends AppCompatActivity implements AssetAdapter.QuantityChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private AssetAdapter adapter;
    private List<Asset> assetList;
    private Button buttonAddAsset;
    private Toolbar toolbar;
    private EditText editTextSearch;
    private Spinner spinnerJurusan;
    private TextView textViewSummary;
    private Button buttonSubmitBorrowing;
    private Map<Integer, Integer> cart = new HashMap<>();
    private String selectedJurusan = "all";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        // Initialize views
        initializeViews();

        // Setup toolbar dengan pengecekan null
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup role-based UI
        setupRoleBasedUI();

        // Setup listeners
        setupListeners();

        // Load data
        assetList = new ArrayList<>();
        loadAssets();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewAssets);
        buttonAddAsset = findViewById(R.id.buttonAddAsset);
        editTextSearch = findViewById(R.id.editTextSearch);
        spinnerJurusan = findViewById(R.id.spinnerJurusan);
        textViewSummary = findViewById(R.id.textViewSummary);
        buttonSubmitBorrowing = findViewById(R.id.buttonSubmitBorrowing);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Daftar Aset");
                // Add hamburger menu icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            } else {
                // Fallback: set title langsung di toolbar
                toolbar.setTitle("Daftar Aset");
            }
        } else {
            // Jika toolbar null, set title di action bar default
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Daftar Aset");
            } else {
                setTitle("Daftar Aset");
            }
        }
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupRoleBasedUI() {
        String role = SharedPrefManager.getInstance(this).getUserRole();
        if ("students".equals(role)) {
            buttonAddAsset.setVisibility(View.GONE);
            View footerLayout = findViewById(R.id.footerLayout);
            if (footerLayout != null) {
                footerLayout.setVisibility(View.VISIBLE);
            }
            // Set student navigation menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navigation_menu);
        } else {
            buttonAddAsset.setVisibility(View.VISIBLE);
            View footerLayout = findViewById(R.id.footerLayout);
            if (footerLayout != null) {
                footerLayout.setVisibility(View.GONE);
            }
            // Set officer navigation menu
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.officer_navigation_menu);
        }

        // Setup navigation view
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupListeners() {
        // Button listeners
        buttonAddAsset.setOnClickListener(v -> {
            Intent intent = new Intent(AssetListActivity.this, AddAssetActivity.class);
            startActivity(intent);
        });

        buttonSubmitBorrowing.setOnClickListener(v -> showConfirmationDialog());

        // Search listener
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAssets(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Spinner listener
        if (spinnerJurusan != null) {
            spinnerJurusan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getItemAtPosition(position).toString();
                    selectedJurusan = "Semua Program Studi".equals(selected) ? "all" : selected;
                    loadAssets();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            String role = SharedPrefManager.getInstance(this).getUserRole();
            Intent intent;
            if ("students".equals(role)) {
                intent = new Intent(this, StudentDashboardActivity.class);
            } else {
                intent = new Intent(this, OfficerDashboardActivity.class);
            }
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_borrow_assets) {
            // Already on this page, do nothing
        } else if (id == R.id.nav_my_borrowings) {
            Intent intent = new Intent(this, BorrowingStatusActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_asset_management) {
            // Already on asset management page, do nothing
        } else if (id == R.id.nav_user_management) {
            Intent intent = new Intent(this, UserManagementActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_borrowing_management) {
            Intent intent = new Intent(this, OfficerBorrowingManagementActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cart.clear();
            updateSummary();
        }
    }

    /**
     * Method untuk memuat daftar aset dari API Laravel menggunakan Volley.
     * Menggunakan JsonObjectRequest untuk mendapatkan response JSON dari endpoint /api/assets.
     * Response Laravel memiliki struktur: {success: boolean, message: string, data: JSONArray}
     */
    public void loadAssets() {
        // Kosongkan list aset sebelum memuat data baru
        assetList.clear();

        String url = Constants.GET_ASSETS_URL;
        if (selectedJurusan != null && !selectedJurusan.equals("all")) {
            url += "?jurusan=" + selectedJurusan;
        }

        // Log untuk menandai mulai loading aset
        Log.d("AssetListActivity", "Memulai loading aset dari API Laravel: " + url);

        // Membuat JsonObjectRequest untuk GET request ke endpoint /api/assets
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, // Method HTTP GET
                url, // URL endpoint dari Constants
                null, // Body request null karena GET tidak memerlukan body
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Callback ketika response berhasil diterima
                        try {
                            // Parsing response JSON Laravel
                            boolean success = response.getBoolean("success"); // Cek apakah request berhasil
                            String message = response.getString("message"); // Pesan dari server

                            Log.d("AssetListActivity", "Response success: " + success + ", message: " + message);

                            if (success) {
                                // Jika berhasil, ambil array data aset
                                JSONArray data = response.getJSONArray("data");

                                // Loop melalui setiap objek aset dalam array
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject assetObject = data.getJSONObject(i);

                                    // Ekstrak field-field dari objek aset
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
                                    String photoUrl = assetObject.optString("photo_url", "");

                                    // Buat objek Asset dari data yang diekstrak
                                    Asset asset = new Asset(id, namaBarang, kodeBarang, jumlahStok, lokasiBarang, jurusanBarang, merk, hargaSatuan, sumber, tahun, deskripsi);
                                    asset.setPhotoUrl(photoUrl);

                                    // Tambahkan aset ke list
                                    assetList.add(asset);

                                    // Log detail aset untuk debugging
                                    Log.d("AssetListActivity", "Aset dimuat: " + namaBarang + " (ID: " + id + ")");
                                }

                                // Set adapter untuk RecyclerView dan tampilkan data
                                adapter = new AssetAdapter(AssetListActivity.this, assetList, AssetListActivity.this);
                                recyclerView.setAdapter(adapter);

                                // Log jumlah aset yang berhasil dimuat
                                Log.d("AssetListActivity", "Total aset dimuat: " + assetList.size());
                            } else {
                                // Jika success false, tampilkan pesan error dari server
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Log.e("AssetListActivity", "Gagal memuat aset: " + message);
                            }
                        } catch (JSONException e) {
                            // Tangani error parsing JSON
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AssetListActivity", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Callback ketika terjadi error network atau server
                        String errorMsg = error.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Network error occurred";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("AssetListActivity", "Error response: " + errorMsg);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPrefManager.getInstance(AssetListActivity.this).getToken();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Tambahkan request ke RequestQueue menggunakan VolleySingleton
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onQuantityChange(int assetId, int quantity) {
        if (quantity > 0) {
            cart.put(assetId, quantity);
        } else {
            cart.remove(assetId);
        }
        updateSummary();
    }

    private void updateSummary() {
        if (textViewSummary == null) return;

        int types = cart.size();
        int total = 0;
        for (int q : cart.values()) {
            total += q;
        }
        textViewSummary.setText(types + " Jenis Barang | Total " + total + " Unit");

        if (buttonSubmitBorrowing != null) {
            buttonSubmitBorrowing.setEnabled(total > 0);
        }
    }

    private void filterAssets(String query) {
        if (adapter == null) return;

        List<Asset> filteredList = new ArrayList<>();
        for (Asset asset : assetList) {
            if (asset.getNamaBarang().toLowerCase().contains(query.toLowerCase()) ||
                    asset.getKodeBarang().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(asset);
            }
        }
        adapter.updateData(filteredList, 0);
    }

    private void showConfirmationDialog() {
        if (cart.isEmpty()) {
            Toast.makeText(this, "Tidak ada barang yang dipilih", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Peminjaman");

        StringBuilder items = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            for (Asset asset : assetList) {
                if (asset.getId() == entry.getKey()) {
                    items.append(asset.getNamaBarang()).append(" x").append(entry.getValue()).append("\n");
                    break;
                }
            }
        }
        builder.setMessage("Barang yang dipinjam:\n" + items.toString() + "\nApakah Anda yakin?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            // Build JSONArray for cart
            JSONArray itemsArray = new JSONArray();
            StringBuilder selectedText = new StringBuilder("Selected Items:\n");
            try {
                for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                    JSONObject item = new JSONObject();
                    item.put("asset_id", entry.getKey());
                    item.put("quantity", entry.getValue());
                    itemsArray.put(item);
                    for (Asset asset : assetList) {
                        if (asset.getId() == entry.getKey()) {
                            selectedText.append(asset.getNamaBarang()).append(" x").append(entry.getValue()).append("\n");
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(AssetListActivity.this, BorrowConfirmActivity.class);
            intent.putExtra("cart", itemsArray.toString());
            intent.putExtra("selected_items_text", selectedText.toString());
            startActivityForResult(intent, 1);
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }
}