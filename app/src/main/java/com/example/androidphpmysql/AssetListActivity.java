package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetListActivity extends AppCompatActivity {

    private static final String TAG = "AssetListActivity";
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private RecyclerView recyclerView;
    private AssetAdapter adapter;
    private List<Asset> assetList;
    private List<Asset> fullAssetList;
    private ProgressDialog progressDialog;

    // Activity result launcher for file picker
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daftar Aset");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewAssets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assetList = new ArrayList<>();
        fullAssetList = new ArrayList<>();
        adapter = new AssetAdapter(this, assetList);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Initialize file picker launcher
        initializeFilePicker();

        loadAssets();
    }

    private void loadAssets() {
        progressDialog.setMessage("Loading assets...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.URL_GET_ASSETS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "GET_ASSETS_RESPONSE: " + response);

                        // Validate response before parsing JSON
                        if (response == null || response.trim().isEmpty()) {
                            Log.e(TAG, "Empty response from server");
                            Toast.makeText(getApplicationContext(),
                                    "Empty response from server", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if response is HTML (error page) instead of JSON
                        String trimmedResponse = response.trim();
                        if (trimmedResponse.startsWith("<") || trimmedResponse.contains("<!DOCTYPE") ||
                            trimmedResponse.contains("<html") || trimmedResponse.contains("<br")) {
                            Log.e(TAG, "Server returned HTML instead of JSON: " + response);
                            Toast.makeText(getApplicationContext(),
                                    "Server error: Database connection failed. Please check server configuration.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            String responseTrimmed = response.trim();
                            JSONArray jsonArray;
                            if (responseTrimmed.startsWith("[")) {
                                jsonArray = new JSONArray(responseTrimmed);
                            } else {
                                JSONObject jsonObject = new JSONObject(responseTrimmed);
                                if (jsonObject.has("assets")) {
                                    jsonArray = jsonObject.getJSONArray("assets");
                                } else if (jsonObject.has("error")) {
                                    String errorMessage = jsonObject.getString("error");
                                    Log.e(TAG, "Server error: " + errorMessage);
                                    Toast.makeText(getApplicationContext(),
                                            "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    Log.e(TAG, "Unexpected response format");
                                    Toast.makeText(getApplicationContext(),
                                            "Unexpected response from server", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            assetList.clear();
                            fullAssetList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Asset asset = new Asset(
                                        obj.optInt("id", -1),
                                        obj.optString("name", ""), // PHP uses 'name'
                                        obj.optString("code", ""), // PHP uses 'code' for code
                                        obj.optString("stock", "0"), // PHP uses 'stock' for jumlah_stok
                                        obj.optString("lokasi", ""), // PHP uses 'lokasi' for lokasi_barang
                                        obj.optString("jurusan", ""), // PHP uses 'jurusan' for categories
                                        obj.optString("merk", ""),
                                        obj.optDouble("harga_satuan", 0.0), // PHP uses 'harga_satuan' for value
                                        obj.optString("sumber", ""),
                                        obj.optString("tahun", ""),
                                        obj.optString("deskripsi", "") // PHP uses 'deskripsi' for description
                                );
                                assetList.add(asset);
                                fullAssetList.add(asset);
                            }
                            adapter.notifyDataSetChanged();
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
                        Toast.makeText(getApplicationContext(),
                                "Error loading assets", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(com.android.volley.NetworkResponse response) {
                if (response.statusCode == 500) {
                    // Server error, possibly DB connection error
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Tidak dapat terhubung ke database", Toast.LENGTH_LONG).show());
                }
                return super.parseNetworkResponse(response);
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_asset, menu);

        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by name or code");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAssets(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuAddAsset) {
            startActivity(new Intent(this, AddAssetActivity.class));
            return true;
        } else if (id == R.id.menuFilter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.menuImportExcel) {
            importAssetsFromExcel();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssets(); // Reload assets when returning to this activity
    }

    private void filterAssets(String query) {
        assetList.clear();
        if (query.isEmpty()) {
            assetList.addAll(fullAssetList);
        } else {
            for (Asset asset : fullAssetList) {
                if (asset.getNamaBarang().toLowerCase().contains(query.toLowerCase()) ||
                    asset.getKodeBarang().toLowerCase().contains(query.toLowerCase())) {
                    assetList.add(asset);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Assets");
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        Spinner spinnerTahun = view.findViewById(R.id.spinnerTahun);
        Spinner spinnerJurusan = view.findViewById(R.id.spinnerJurusan);

        // Populate spinners
        List<String> tahunList = new ArrayList<>();
        tahunList.add("All");
        for (Asset asset : fullAssetList) {
            if (!tahunList.contains(asset.getTahun())) {
                tahunList.add(asset.getTahun());
            }
        }
        ArrayAdapter<String> tahunAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tahunList);
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);

        List<String> jurusanList = new ArrayList<>();
        jurusanList.add("All");
        for (Asset asset : fullAssetList) {
            if (!jurusanList.contains(asset.getJurusanBarang())) {
                jurusanList.add(asset.getJurusanBarang());
            }
        }
        ArrayAdapter<String> jurusanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jurusanList);
        jurusanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJurusan.setAdapter(jurusanAdapter);

        builder.setView(view);
        builder.setPositiveButton("Apply", (dialog, which) -> {
            String selectedTahun = spinnerTahun.getSelectedItem().toString();
            String selectedJurusan = spinnerJurusan.getSelectedItem().toString();
            filterAssetsByCriteria(selectedTahun, selectedJurusan);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void filterAssetsByCriteria(String tahun, String jurusan) {
        assetList.clear();
        for (Asset asset : fullAssetList) {
            boolean matchesTahun = "All".equals(tahun) || asset.getTahun().equals(tahun);
            boolean matchesJurusan = "All".equals(jurusan) || asset.getJurusanBarang().equals(jurusan);
            if (matchesTahun && matchesJurusan) {
                assetList.add(asset);
            }
        }
        adapter.notifyDataSetChanged();
    }



    /**
     * Import assets from Excel file
     */
    private void importAssetsFromExcel() {
        if (checkStoragePermission()) {
            openFilePicker();
        } else {
            requestStoragePermission();
        }
    }

    /**
     * Check if storage permission is granted
     */
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+, check if MANAGE_EXTERNAL_STORAGE permission is granted
            return Environment.isExternalStorageManager();
        } else {
            // For older versions, check READ_EXTERNAL_STORAGE permission
            return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Request storage permission
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_STORAGE_PERMISSION);
        }
    }



    /**
     * Open file picker for Excel import
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            filePickerLauncher.launch(Intent.createChooser(intent, getString(R.string.select_excel_file)));
        } catch (Exception e) {
            Log.e(TAG, "Error opening file picker", e);
            Toast.makeText(this, R.string.import_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle file picker result - FIXED VERSION
     */
    /**
     * Handle file picker result - COMPLETE FIXED VERSION
     */
    private void handleFilePickerResult(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Importing from Excel...");
        progressDialog.show();

        new Thread(() -> {
            try {
                // Step 1: Import from Excel (this works perfectly)
                List<Asset> importedAssets = ExcelUtils.importAssetsFromExcel(this, uri);

                if (importedAssets == null || importedAssets.isEmpty()) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Import gagal: Tidak ada data yang bisa diimpor", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Step 2: Save each imported asset to database
                runOnUiThread(() -> {
                    progressDialog.setMessage("Saving to database... (0/" + importedAssets.size() + ")");
                });

                int savedCount = 0;
                int errorCount = 0;

                for (int i = 0; i < importedAssets.size(); i++) {
                    Asset asset = importedAssets.get(i);

                    final int currentIndex = i;
                    runOnUiThread(() -> {
                        progressDialog.setMessage("Saving to database... (" + (currentIndex + 1) + "/" + importedAssets.size() + ")");
                    });

                    try {
                        boolean success = saveAssetToDatabase(asset);
                        if (success) {
                            savedCount++;
                        } else {
                            errorCount++;
                        }

                        // Small delay to prevent overwhelming the server
                        Thread.sleep(100);

                    } catch (Exception e) {
                        Log.e(TAG, "Error saving asset: " + asset.getNamaBarang(), e);
                        errorCount++;
                    }
                }

                final int finalSavedCount = savedCount;
                final int finalErrorCount = errorCount;

                runOnUiThread(() -> {
                    progressDialog.dismiss();

                    String message = "Import completed!\n";
                    message += "Successfully saved: " + finalSavedCount + "\n";
                    if (finalErrorCount > 0) {
                        message += "Errors: " + finalErrorCount;
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    // Step 3: Refresh the asset list from server
                    loadAssets();
                });

            } catch (Exception e) {
                Log.e(TAG, "Import error", e);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Import gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * Save a single asset to database via API
     */
    private boolean saveAssetToDatabase(Asset asset) {
        final boolean[] success = {false};
        final Object lock = new Object();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_ASSET, // Fixed: Use correct constant name
                response -> {
                    Log.d(TAG, "Save response: " + response);

                    // Validate response before parsing JSON
                    if (response == null || response.trim().isEmpty()) {
                        Log.e(TAG, "Empty response from server");
                        success[0] = false;
                        synchronized (lock) {
                            lock.notify();
                        }
                        return;
                    }

                    // Check if response is HTML (error page) instead of JSON
                    String trimmedResponse = response.trim();
                    if (trimmedResponse.startsWith("<") || trimmedResponse.contains("<!DOCTYPE") ||
                        trimmedResponse.contains("<html") || trimmedResponse.contains("<br")) {
                        Log.e(TAG, "Server returned HTML instead of JSON: " + response);
                        success[0] = false;
                        synchronized (lock) {
                            lock.notify();
                        }
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(trimmedResponse);
                        if (jsonObject.getBoolean("error")) {
                            Log.e(TAG, "Server error saving asset: " + jsonObject.getString("message"));
                            success[0] = false;
                        } else {
                            success[0] = true;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error", e);
                        success[0] = false;
                    }

                    synchronized (lock) {
                        lock.notify();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error saving asset", error);
                    success[0] = false;
                    synchronized (lock) {
                        lock.notify();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", asset.getNamaBarang());
                params.put("code", asset.getKodeBarang());
                params.put("stock", asset.getJumlahStok());
                params.put("lokasi", asset.getLokasiBarang());
                params.put("jurusan", asset.getJurusanBarang());
                params.put("merk", asset.getMerk());
                params.put("harga_satuan", String.valueOf(asset.getHargaSatuan()));
                params.put("sumber", asset.getSumber());
                params.put("tahun", asset.getTahun());
                params.put("deskripsi", asset.getDeskripsi());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        // Wait for the request to complete
        synchronized (lock) {
            try {
                lock.wait(15000); // Wait up to 15 seconds
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
                return false;
            }
        }

        return success[0];
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.file_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initialize file picker launcher
     */
    private void initializeFilePicker() {
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    handleFilePickerResult(uri);
                }
            }
        );
    }
}
