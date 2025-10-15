package com.example.androidphpmysql;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminBorrowingManagementActivity extends AppCompatActivity implements AdminBorrowingAdapter.OnBorrowingActionListener {

    private RecyclerView recyclerViewBorrowings;
    private EditText editTextSearch;
    private Button buttonFilter;
    private TextView emptyStateText;
    private AdminBorrowingAdapter adapter;
    private List<Borrowing> borrowingList = new ArrayList<>();

    // Filter parameters
    private String currentSearch = "";
    private String currentStatus = "";
    private String currentJurusan = "";
    private String currentClass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_borrowing_management);

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupFilterButton();

        loadBorrowings();
    }

    private void initializeViews() {
        recyclerViewBorrowings = findViewById(R.id.recyclerViewBorrowings);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonFilter = findViewById(R.id.buttonFilter);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupRecyclerView() {
        recyclerViewBorrowings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBorrowingAdapter(this, borrowingList, this);
        recyclerViewBorrowings.setAdapter(adapter);
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                loadBorrowings();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButton() {
        buttonFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_borrowing_filter);

        Spinner spinnerStatus = dialog.findViewById(R.id.spinnerStatus);
        Spinner spinnerJurusan = dialog.findViewById(R.id.spinnerJurusan);
        Spinner spinnerClass = dialog.findViewById(R.id.spinnerClass);
        Button buttonReset = dialog.findViewById(R.id.buttonReset);
        Button buttonApply = dialog.findViewById(R.id.buttonApply);

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"", "pending", "approved", "rejected", "returned", "partially_approved", "partial"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        setSpinnerSelection(spinnerStatus, currentStatus);

        // Setup jurusan spinner (for admin only)
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        if ("admin".equals(sharedPrefManager.getUser().getRole())) {
            ArrayAdapter<String> jurusanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                    new String[]{"", "Teknik Informatika", "Teknik Elektro", "Teknik Mesin", "Teknik Sipil"});
            jurusanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerJurusan.setAdapter(jurusanAdapter);
            setSpinnerSelection(spinnerJurusan, currentJurusan);
        } else {
            spinnerJurusan.setVisibility(View.GONE);
        }

        // Setup class spinner
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"", "10 TKJ", "11 TKJ", "12 TKJ", "10 RPL", "11 RPL", "12 RPL"});
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);
        setSpinnerSelection(spinnerClass, currentClass);

        buttonReset.setOnClickListener(v -> {
            currentStatus = "";
            currentJurusan = "";
            currentClass = "";
            loadBorrowings();
            dialog.dismiss();
        });

        buttonApply.setOnClickListener(v -> {
            currentStatus = spinnerStatus.getSelectedItem().toString();
            if ("admin".equals(sharedPrefManager.getUser().getRole())) {
                currentJurusan = spinnerJurusan.getSelectedItem().toString();
            }
            currentClass = spinnerClass.getSelectedItem().toString();
            loadBorrowings();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null && value != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }

    private void loadBorrowings() {
        String url = Constants.BASE_URL + "borrowings";
        StringBuilder urlBuilder = new StringBuilder(url);

        List<String> params = new ArrayList<>();
        if (!currentSearch.isEmpty()) params.add("search=" + currentSearch);
        if (!currentStatus.isEmpty()) params.add("status=" + currentStatus);
        if (!currentJurusan.isEmpty()) params.add("jurusan=" + currentJurusan);
        if (!currentClass.isEmpty()) params.add("class=" + currentClass);

        if (!params.isEmpty()) {
            urlBuilder.append("?").append(String.join("&", params));
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlBuilder.toString(), null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");
                            borrowingList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Borrowing borrowing = new Borrowing();
                                borrowing.setId(obj.getInt("id"));
                                borrowing.setBorrowDate(obj.getString("borrow_date"));
                                borrowing.setReturnDate(obj.getString("return_date"));
                                borrowing.setStatus(obj.getString("status"));
                                borrowing.setTujuan(obj.optString("tujuan", ""));

                                // Student
                                JSONObject studentObj = obj.getJSONObject("student");
                                Student student = new Student();
                                student.setId(studentObj.getInt("id"));
                                student.setName(studentObj.getString("name"));

                                // School class
                                if (studentObj.has("school_class") && !studentObj.isNull("school_class")) {
                                    JSONObject classObj = studentObj.getJSONObject("school_class");
                                    SchoolClass schoolClass = new SchoolClass();
                                    schoolClass.setId(classObj.getInt("id"));
                                    schoolClass.setName(classObj.getString("name"));
                                    student.setSchoolClass(schoolClass);
                                }

                                // User
                                if (studentObj.has("user")) {
                                    JSONObject userObj = studentObj.getJSONObject("user");
                                    User user = new User();
                                    user.setId(userObj.getInt("id"));
                                    user.setName(userObj.getString("name"));
                                    if (userObj.has("profile_photo")) {
                                        user.setProfilePhoto(userObj.getString("profile_photo"));
                                    }
                                    student.setUser(user);
                                }

                                borrowing.setStudent(student);

                                // Items
                                JSONArray itemsArray = obj.getJSONArray("items");
                                List<BorrowingItem> items = new ArrayList<>();
                                for (int j = 0; j < itemsArray.length(); j++) {
                                    JSONObject itemObj = itemsArray.getJSONObject(j);
                                    BorrowingItem item = new BorrowingItem();
                                    item.setId(itemObj.getInt("id"));
                                    item.setQuantity(itemObj.getInt("quantity"));
                                    item.setStatus(itemObj.getString("status"));

                                    // Commodity
                                    JSONObject commodityObj = itemObj.getJSONObject("commodity");
                                    Commodity commodity = new Commodity();
                                    commodity.setId(commodityObj.getInt("id"));
                                    commodity.setName(commodityObj.getString("name"));
                                    commodity.setJurusan(commodityObj.optString("jurusan", ""));

                                    if (itemObj.has("photo_path")) {
                                        item.setPhotoUrl(Constants.STORAGE_BASE + itemObj.getString("photo_path"));
                                    }

                                    item.setCommodity(commodity);
                                    items.add(item);
                                }
                                borrowing.setItems(items);

                                // Return photos
                                if (obj.has("return_photos") && !obj.isNull("return_photos")) {
                                    JSONArray returnPhotosArray = obj.getJSONArray("return_photos");
                                    borrowing.setReturnPhoto(returnPhotosArray);
                                }

                                borrowingList.add(borrowing);
                            }

                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error loading borrowings", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                headers.put("Authorization", "Bearer " + sharedPrefManager.getToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateEmptyState() {
        if (borrowingList.isEmpty()) {
            recyclerViewBorrowings.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBorrowings.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApprove(Borrowing borrowing) {
        showItemSelectionDialog(borrowing, "approve");
    }

    @Override
    public void onReject(Borrowing borrowing) {
        showItemSelectionDialog(borrowing, "reject");
    }

    @Override
    public void onReturn(Borrowing borrowing) {
        performAction(borrowing.getId(), "admin-return", null);
    }

    @Override
    public void onViewPhoto(String photoUrl) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra("image_url", photoUrl);
        startActivity(intent);
    }

    @Override
    public void onViewReturnPhotos(Borrowing borrowing) {
        JSONArray returnPhotos = borrowing.getReturnPhoto();
        if (returnPhotos != null && returnPhotos.length() > 0) {
            // For multiple photos, you might want to create a gallery or show the first one
            try {
                String firstPhotoUrl = returnPhotos.getString(0);
                Intent intent = new Intent(this, ImageViewerActivity.class);
                intent.putExtra("image_url", Constants.STORAGE_BASE + firstPhotoUrl);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading return photos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No return photos available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showItemSelectionDialog(Borrowing borrowing, String action) {
        // Filter pending items
        List<BorrowingItem> pendingItems = new ArrayList<>();
        for (BorrowingItem item : borrowing.getItemsList()) {
            if ("pending".equals(item.getStatus())) {
                pendingItems.add(item);
            }
        }

        // For officers, filter to only items from their jurusan
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        User user = sharedPrefManager.getUser();
        if ("officer".equals(user.getRole())) {
            List<BorrowingItem> filteredItems = new ArrayList<>();
            for (BorrowingItem item : pendingItems) {
                if (user.getJurusan() != null && user.getJurusan().equals(item.getCommodity().getJurusan())) {
                    filteredItems.add(item);
                }
            }
            pendingItems = filteredItems;
        }

        if (pendingItems.isEmpty()) {
            Toast.makeText(this, "Tidak ada item yang pending untuk " + action, Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate dialog based on action
        int layoutRes = "approve".equals(action) ? R.layout.dialog_select_items_approve : R.layout.dialog_select_items_reject;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(layoutRes);

        LinearLayout layoutItems = dialog.findViewById(R.id.layoutItems);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);

        // Populate items
        for (BorrowingItem item : pendingItems) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_checkbox, layoutItems, false);
            CheckBox checkBox = itemView.findViewById(R.id.checkboxItem);
            TextView textItemInfo = itemView.findViewById(R.id.textItemInfo);

            textItemInfo.setText(item.getCommodity().getName() + " (" + item.getQuantity() + ")");
            checkBox.setTag(item.getId()); // Tag with item ID
            checkBox.setChecked(true); // Default select all

            layoutItems.addView(itemView);
        }

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonConfirm.setOnClickListener(v -> {
            List<Integer> selectedItemIds = new ArrayList<>();
            for (int i = 0; i < layoutItems.getChildCount(); i++) {
                View child = layoutItems.getChildAt(i);
                CheckBox cb = child.findViewById(R.id.checkboxItem);
                if (cb.isChecked()) {
                    selectedItemIds.add((Integer) cb.getTag());
                }
            }

            if (selectedItemIds.isEmpty()) {
                Toast.makeText(this, "Pilih minimal satu item", Toast.LENGTH_SHORT).show();
                return;
            }

            performAction(borrowing.getId(), action, selectedItemIds);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void performAction(int borrowingId, String action, List<Integer> itemIds) {
        String url = Constants.BASE_URL + "borrowings/" + borrowingId + "/" + action;

        JSONObject requestBody = new JSONObject();
        if (itemIds != null) {
            JSONArray itemsArray = new JSONArray(itemIds);
            try {
                requestBody.put("items", itemsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int method = Request.Method.POST;

        JsonObjectRequest request = new JsonObjectRequest(method, url, requestBody,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            loadBorrowings(); // Refresh the list
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error performing action", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                headers.put("Authorization", "Bearer " + sharedPrefManager.getToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
