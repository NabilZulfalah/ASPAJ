package com.example.androidphpmysql;

import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();

        loadUsers();
    }

    private void loadUsers() {
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
                            boolean success = obj.getBoolean("success");
                            String message = obj.getString("message");
                            if (success) {
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

                                adapter = new UserAdapter(UserManagementActivity.this, userList, UserManagementActivity.this);
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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onEdit(User user) {
        Intent intent = new Intent(this, AddUserActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        intent.putExtra("user_email", user.getEmail());
        intent.putExtra("user_role", user.getRole());
        intent.putExtra("user_approval_status", user.getApprovalStatus());
        startActivity(intent);
    }

    @Override
    public void onDelete(User user) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Hapus User")
                .setMessage("Apakah Anda yakin ingin menghapus user ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteUser(user.getId()))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteUser(int userId) {
        String url = Constants.URL_DELETE_USER + "/" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.getBoolean("success");
                        String message = obj.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            loadUsers();
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

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
