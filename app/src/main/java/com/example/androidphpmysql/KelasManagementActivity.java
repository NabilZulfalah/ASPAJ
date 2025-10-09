package com.example.androidphpmysql;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KelasManagementActivity extends AppCompatActivity implements SchoolClassAdapter.OnSchoolClassActionListener {

    private RecyclerView recyclerView;
    private SchoolClassAdapter adapter;
    private List<SchoolClass> schoolClassList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas_management);

        recyclerView = findViewById(R.id.recyclerViewKelas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        schoolClassList = new ArrayList<>();

        loadSchoolClasses();
    }

    private void loadSchoolClasses() {
        schoolClassList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GET_KELAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean success = obj.getBoolean("success");
                    String message = obj.getString("message");
                    if (success) {
                        JSONArray schoolClasses = obj.getJSONArray("data");

                        for (int i = 0; i < schoolClasses.length(); i++) {
                            JSONObject schoolClassObject = schoolClasses.getJSONObject(i);

                            int id = schoolClassObject.optInt("id", 0);
                            String name = schoolClassObject.optString("name", "");
                            String level = schoolClassObject.optString("level", "");
                            String programStudy = schoolClassObject.optString("program_study", "");
                            int capacity = schoolClassObject.optInt("capacity", 0);
                            String description = schoolClassObject.optString("description", "");

                            SchoolClass schoolClass = new SchoolClass(id, name, level, programStudy, capacity, description);

                            schoolClassList.add(schoolClass);
                        }

                        adapter = new SchoolClassAdapter(KelasManagementActivity.this, schoolClassList, KelasManagementActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.getMessage() != null ? error.getMessage() : "Network Error Occurred";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onEdit(SchoolClass schoolClass) {
        Intent intent = new Intent(this, AddKelasActivity.class);
        intent.putExtra("kelas_id", schoolClass.getId());
        intent.putExtra("kelas_name", schoolClass.getName());
        intent.putExtra("kelas_level", schoolClass.getLevel());
        intent.putExtra("kelas_program_study", schoolClass.getProgramStudy());
        intent.putExtra("kelas_capacity", schoolClass.getCapacity());
        intent.putExtra("kelas_description", schoolClass.getDescription());
        startActivity(intent);
    }

    @Override
    public void onDelete(SchoolClass schoolClass) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Kelas")
                .setMessage("Apakah Anda yakin ingin menghapus kelas ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteKelas(schoolClass.getId()))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteKelas(int kelasId) {
        String url = Constants.URL_DELETE_KELAS + "/" + kelasId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            loadSchoolClasses();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Network Error Occurred", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPrefManager.getInstance(getApplicationContext()).getToken();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                // No parameters needed for DELETE request with ID in URL
                return null;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
