package com.example.androidphpmysql;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.android.volley.RequestQueue;
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

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private static final int REQUEST_ADD_USER = 1;
    private static final int REQUEST_EDIT_USER = 2;

    private Button buttonAddUser, buttonSearch, buttonPrevious, buttonNext;
    private EditText editTextSearch;
    private Spinner spinnerRoleFilter;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> filteredUserList = new ArrayList<>();
    private List<User> displayedUserList = new ArrayList<>();
    private TextView textViewPaginationInfo;

    private int currentPage = 0;
    private int itemsPerPage = 10;
    private int totalPages = 0;

    private String selectedRoleFilter = "Semua Role";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        buttonAddUser = findViewById(R.id.button_add_user);
        buttonSearch = findViewById(R.id.button_search);
        editTextSearch = findViewById(R.id.edittext_search);
        spinnerRoleFilter = findViewById(R.id.spinner_role_filter);
        recyclerViewUsers = findViewById(R.id.recyclerview_users);
        buttonPrevious = findViewById(R.id.button_previous);
        buttonNext = findViewById(R.id.button_next);
        textViewPaginationInfo = findViewById(R.id.textview_pagination_info);

        // Setup RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, displayedUserList, this);
        recyclerViewUsers.setAdapter(userAdapter);

        // Setup role filter spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleFilter.setAdapter(adapter);
        spinnerRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoleFilter = parent.getItemAtPosition(position).toString();
                filterUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoleFilter = "Semua Role";
                filterUsers();
            }
        });

        buttonAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
            startActivityForResult(intent, REQUEST_ADD_USER);
            Toast.makeText(UserListActivity.this, "Navigating to Add User screen", Toast.LENGTH_SHORT).show();
        });

        buttonSearch.setOnClickListener(v -> filterUsers());

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

        // Load initial user data (this could be from backend API)
        loadUsers();
    }

    private void loadUsers() {
        String url = Constants.URL_GET_USERS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("error")) {
                                Toast.makeText(UserListActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray usersArray = response.getJSONArray("users");
                            userList.clear();
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObj = usersArray.getJSONObject(i);
                                User user = new User(
                                        userObj.getString("name"),
                                        userObj.getString("email"),
                                        userObj.getString("role"),
                                        userObj.getString("status")
                                );
                                userList.add(user);
                            }
                            filterUsers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(UserListActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Toast.makeText(UserListActivity.this, "Delete user request sent", Toast.LENGTH_SHORT).show();

        RequestHandler.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void filterUsers() {
        String searchText = editTextSearch.getText().toString().toLowerCase().trim();

        filteredUserList.clear();

        for (User user : userList) {
            boolean matchesSearch = TextUtils.isEmpty(searchText) ||
                    user.getName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);

            boolean matchesRole = selectedRoleFilter.equals("Semua Role") ||
                    user.getRole().equalsIgnoreCase(selectedRoleFilter);

            if (matchesSearch && matchesRole) {
                filteredUserList.add(user);
            }
        }

        totalPages = (int) Math.ceil((double) filteredUserList.size() / itemsPerPage);
        currentPage = 0;
        updatePagination();
    }

    @Override
    public void onEdit(User user) {
        Intent intent = new Intent(this, AddUserActivity.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, REQUEST_EDIT_USER);
    }

    @Override
    public void onDelete(User user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETE_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.getBoolean("error")) {
                                loadUsers();
                                Toast.makeText(UserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserListActivity.this, "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(UserListActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserListActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updatePagination() {
        displayedUserList.clear();
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredUserList.size());
        for (int i = startIndex; i < endIndex; i++) {
            displayedUserList.add(filteredUserList.get(i));
        }
        userAdapter.updateData(displayedUserList);

        // Update pagination info
        textViewPaginationInfo.setText("Page " + (currentPage + 1) + " of " + totalPages);

        // Enable/disable buttons
        buttonPrevious.setEnabled(currentPage > 0);
        buttonNext.setEnabled(currentPage < totalPages - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            User user = (User) data.getSerializableExtra("user");
            if (requestCode == REQUEST_ADD_USER) {
                userList.add(user);
            } else if (requestCode == REQUEST_EDIT_USER) {
                // Update user in list
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getEmail().equals(user.getEmail())) {
                        userList.set(i, user);
                        break;
                    }
                }
            }
            filterUsers();
        }
    }
}
