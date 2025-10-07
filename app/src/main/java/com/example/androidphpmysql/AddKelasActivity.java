package com.example.androidphpmysql;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class AddKelasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddKelasActivity";

    private EditText editTextName, editTextLevel, editTextDescription, editTextCapacity;
    private Spinner spinnerProgramStudy;
    private Button buttonSave;
    private ProgressDialog progressDialog;

    private boolean isEditMode = false;
    private Kelas kelasToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kelas);

        // Check if editing
        Intent intent = getIntent();
        if (intent.hasExtra("kelas")) {
            isEditMode = true;
            kelasToEdit = (Kelas) intent.getSerializableExtra("kelas");
            setTitle("Edit Kelas");
        } else {
            setTitle("Tambah Kelas");
        }

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextLevel = findViewById(R.id.editTextLevel);
        spinnerProgramStudy = findViewById(R.id.spinnerProgramStudy);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        buttonSave = findViewById(R.id.buttonSave);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.program_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgramStudy.setAdapter(adapter);

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Set click listener
        buttonSave.setOnClickListener(this);

        // Populate fields if editing
        if (isEditMode && kelasToEdit != null) {
            populateFields();
        }
    }

    private void populateFields() {
        editTextName.setText(kelasToEdit.getName());
        editTextLevel = findViewById(R.id.editTextLevel);
        editTextLevel.setText(kelasToEdit.getLevel());

        // Set spinner selection
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerProgramStudy.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(kelasToEdit.getProgramStudy())) {
                    spinnerProgramStudy.setSelection(i);
                    break;
                }
            }
        }

        editTextDescription.setText(kelasToEdit.getDescription());
        editTextCapacity.setText(String.valueOf(kelasToEdit.getCapacity()));
    }

    private void saveKelas() {
        String name = editTextName.getText().toString().trim();
        String level = editTextLevel.getText().toString().trim();
        String programStudy = spinnerProgramStudy.getSelectedItem().toString();
        String description = editTextDescription.getText().toString().trim();
        String capacity = editTextCapacity.getText().toString().trim();

        // Validation
        if (name.isEmpty() || programStudy.isEmpty()) {
            Toast.makeText(this, "Nama dan program studi harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(isEditMode ? "Updating Kelas..." : "Adding Kelas...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                isEditMode ? Constants.URL_UPDATE_KELAS : Constants.URL_ADD_KELAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_LONG).show();

                            if (jsonObject.getString("error").equals("false")) {
                                // Return the created/updated kelas
                                Kelas savedKelas = new Kelas();
                                savedKelas.setName(name);
                                savedKelas.setLevel(level);
                                savedKelas.setProgramStudy(programStudy);
                                savedKelas.setDescription(description);
                                if (isEditMode) {
                                    savedKelas.setId(kelasToEdit.getId());
                                }
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("kelas", savedKelas);
                                setResult(RESULT_OK, resultIntent);
                                finish(); // Close activity on success
                            }
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
                                "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("level", level);
                params.put("program_study", programStudy);
                params.put("description", description);
                params.put("capacity", capacity);

                if (isEditMode && kelasToEdit != null) {
                    params.put("id", kelasToEdit.getId());
                }

                Log.d(TAG, "Params: " + params.toString());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSave) {
            saveKelas();
        }
    }
}