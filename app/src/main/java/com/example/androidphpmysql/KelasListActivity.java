package com.example.androidphpmysql;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KelasListActivity extends AppCompatActivity implements KelasAdapter.OnKelasActionListener {

    private static final String TAG = "KelasListActivity";
    private static final String DEBUG_TAG = "DEBUG_IMPORT";
    private static final int REQUEST_ADD_KELAS = 1;
    private static final int REQUEST_EDIT_KELAS = 2;
    private static final int REQUEST_IMPORT_EXCEL = 3;

    private Button buttonAddKelas, buttonSearch, buttonPrevious, buttonNext, buttonImportKelas;
    private EditText editTextSearch;
    private Spinner spinnerProgramFilter;
    private RecyclerView recyclerViewKelas;
    private KelasAdapter kelasAdapter;
    private TextView textViewPaginationInfo;
    private List<Kelas> kelasList = new ArrayList<>();
    private List<Kelas> filteredKelasList = new ArrayList<>();
    private List<Kelas> displayedKelasList = new ArrayList<>();

    private String selectedProgramFilter = "Semua Program Studi";
    private int currentPage = 0;
    private int itemsPerPage = 10;
    private int totalPages = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas_list);

        buttonAddKelas = findViewById(R.id.button_add_kelas);
        buttonImportKelas = findViewById(R.id.button_import_kelas);
        buttonSearch = findViewById(R.id.button_search);
        editTextSearch = findViewById(R.id.edittext_search);
        spinnerProgramFilter = findViewById(R.id.spinner_program_filter);
        recyclerViewKelas = findViewById(R.id.recyclerview_kelas);
        buttonPrevious = findViewById(R.id.button_previous);
        buttonNext = findViewById(R.id.button_next);
        textViewPaginationInfo = findViewById(R.id.textview_pagination_info);

        recyclerViewKelas.setLayoutManager(new LinearLayoutManager(this));
        kelasAdapter = new KelasAdapter(this, displayedKelasList, this, currentPage * itemsPerPage);
        recyclerViewKelas.setAdapter(kelasAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jurusan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgramFilter.setAdapter(adapter);
        spinnerProgramFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProgramFilter = parent.getItemAtPosition(position).toString();
                filterKelas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProgramFilter = "Semua Program Studi";
                filterKelas();
            }
        });

        buttonAddKelas.setOnClickListener(v -> {
            Intent intent = new Intent(KelasListActivity.this, AddKelasActivity.class);
            startActivityForResult(intent, REQUEST_ADD_KELAS);
        });

        buttonImportKelas.setOnClickListener(v -> openFilePicker());


        buttonSearch.setOnClickListener(v -> filterKelas());

        buttonPrevious.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePagination();
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updatePagination();
            }
        });

        loadKelas();
    }

    private void openFilePicker() {
        Log.d(DEBUG_TAG, "Tombol Import ditekan — membuka file picker");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String[] mimeTypes = {
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        try {
            startActivityForResult(Intent.createChooser(intent, "Pilih File Excel"), REQUEST_IMPORT_EXCEL);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Mohon install File Manager terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromExcel(Uri uri) {
        if (uri == null) {
            Log.e(DEBUG_TAG, "URI null, tidak bisa import Excel");
            Toast.makeText(this, "Gagal import: URI kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(DEBUG_TAG, "Mulai import Excel dari URI: " + uri);

        try {
            List<Kelas> importedKelas = ExcelUtils.importKelasFromExcel(this, uri);
            Log.d(DEBUG_TAG, "Selesai import, total data: " + importedKelas.size());

            if (importedKelas.isEmpty()) {
                Toast.makeText(this, "Tidak ada data ditemukan dalam file Excel", Toast.LENGTH_SHORT).show();
                return;
            }

            int addedCount = 0;
            for (Kelas kelas : importedKelas) {
                boolean exists = false;
                for (Kelas existing : kelasList) {
                    if (existing.getName() != null && existing.getName().equals(kelas.getName()) &&
                            existing.getProgramStudy() != null && existing.getProgramStudy().equals(kelas.getProgramStudy())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    kelasList.add(kelas);
                    addedCount++;
                    // Send API request to add kelas to database
                    sendAddKelasRequest(kelas);
                }
            }

            Log.d(DEBUG_TAG, "Berhasil menambahkan " + addedCount + " data baru");

            filterKelas();
            updatePagination();

            Toast.makeText(this, "Berhasil import " + addedCount + " kelas dari Excel", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Gagal import Excel", e);
            Toast.makeText(this, "Error import Excel: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendAddKelasRequest(Kelas kelas) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADD_KELAS,
                response -> {
                    Log.d(DEBUG_TAG, "AddKelas API response: " + response);
                    // Optionally parse response and handle success/failure
                },
                error -> {
                    Log.e(DEBUG_TAG, "AddKelas API error: " + error.getMessage());
                    Toast.makeText(KelasListActivity.this, "Gagal menambahkan kelas ke database: " + kelas.getName(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", kelas.getName());
                params.put("level", kelas.getLevel() != null ? kelas.getLevel() : "");
                params.put("program_study", kelas.getProgramStudy() != null ? kelas.getProgramStudy() : "");
                params.put("capacity", kelas.getCapacity() != null ? kelas.getCapacity() : "30");
                params.put("description", kelas.getDescription() != null ? kelas.getDescription() : "");
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void exportToExcel() {
        if (kelasList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show();
            return;
        }

        String filename = "kelas_export_" + System.currentTimeMillis() + ".xlsx";
        boolean success = ExcelUtils.writeKelasToExcel(this, kelasList, filename);

        if (success) {
            Toast.makeText(this, "Data berhasil diekspor ke Download/" + filename, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Gagal mengekspor data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadKelas() {
        String url = Constants.URL_GET_KELAS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (!success) {
                            Toast.makeText(KelasListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONArray kelasArray = response.getJSONArray("data");
                        kelasList.clear();
                        for (int i = 0; i < kelasArray.length(); i++) {
                            JSONObject kelasObj = kelasArray.getJSONObject(i);

                            String id = String.valueOf(kelasObj.optInt("id", 0));
                            String name = kelasObj.optString("name", "");
                            String level = kelasObj.optString("level", "");
                            String programStudy = kelasObj.optString("program_study", "");
                            String capacity = kelasObj.optString("capacity", "");
                            String description = kelasObj.optString("description", "");
                            String createdAt = kelasObj.optString("created_at", "");
                            String updatedAt = kelasObj.optString("updated_at", "");

                            Kelas kelas = new Kelas(id, name, level, programStudy, capacity, description, createdAt, updatedAt);
                            kelasList.add(kelas);
                        }
                        filterKelas();
                        updatePagination();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(KelasListActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(KelasListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestHandler.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void filterKelas() {
        String searchText = editTextSearch.getText().toString().toLowerCase().trim();

        filteredKelasList.clear();

        for (Kelas kelas : kelasList) {
            boolean matchesSearch = TextUtils.isEmpty(searchText) ||
                    kelas.getName().toLowerCase().contains(searchText) ||
                    kelas.getProgramStudy().toLowerCase().contains(searchText) ||
                    (kelas.getDescription() != null && kelas.getDescription().toLowerCase().contains(searchText));

            boolean matchesProgram = selectedProgramFilter.equals("Semua Program Studi") ||
                    kelas.getProgramStudy().equalsIgnoreCase(selectedProgramFilter);

            if (matchesSearch && matchesProgram) {
                filteredKelasList.add(kelas);
            }
        }

        totalPages = (int) Math.ceil((double) filteredKelasList.size() / itemsPerPage);
        currentPage = 0;
        updatePagination();
    }

    @Override
    public void onEdit(Kelas kelas) {
        Intent intent = new Intent(this, AddKelasActivity.class);
        intent.putExtra("kelas", kelas);
        startActivityForResult(intent, REQUEST_EDIT_KELAS);
    }

    @Override
    public void onDelete(Kelas kelas) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETE_KELAS,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            kelasList.remove(kelas);
                            filterKelas();
                            updatePagination();
                            Toast.makeText(KelasListActivity.this, "Kelas berhasil dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(KelasListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(KelasListActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(KelasListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", kelas.getId());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(DEBUG_TAG, "onActivityResult dipanggil, requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMPORT_EXCEL) {
                Uri uri = data.getData();
                Log.d(DEBUG_TAG, "URI diterima: " + uri);
                if (uri != null) {
                    importFromExcel(uri);
                } else {
                    Log.e(DEBUG_TAG, "URI null dari picker");
                    Toast.makeText(this, "Gagal mendapatkan file Excel", Toast.LENGTH_SHORT).show();
                }
            } else {
                Kelas kelas = (Kelas) data.getSerializableExtra("kelas");
                if (kelas != null) {
                    if (requestCode == REQUEST_ADD_KELAS) {
                        kelasList.add(kelas);
                    } else if (requestCode == REQUEST_EDIT_KELAS) {
                        for (int i = 0; i < kelasList.size(); i++) {
                            if (kelasList.get(i).getId().equals(kelas.getId())) {
                                kelasList.set(i, kelas);
                                break;
                            }
                        }
                    }
                    filterKelas();
                    updatePagination();
                }
            }
        } else {
            Log.w(DEBUG_TAG, "onActivityResult: data null atau resultCode bukan RESULT_OK");
        }
    }

    private void updatePagination() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredKelasList.size());
        displayedKelasList.clear();
        for (int i = startIndex; i < endIndex; i++) {
            displayedKelasList.add(filteredKelasList.get(i));
        }
        kelasAdapter.updateData(displayedKelasList, startIndex);
        int totalItems = filteredKelasList.size();
        int startItem = totalItems > 0 ? startIndex + 1 : 0;
        int endItem = endIndex;
        textViewPaginationInfo.setText("Menampilkan " + startItem + " sampai " + endItem + " dari " + totalItems + " data");
        buttonPrevious.setEnabled(currentPage > 0);
        buttonPrevious.setAlpha(currentPage > 0 ? 1.0f : 0.5f);
        buttonNext.setEnabled(currentPage < totalPages - 1);
        buttonNext.setAlpha(currentPage < totalPages - 1 ? 1.0f : 0.5f);
    }
}