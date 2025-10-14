package com.example.androidphpmysql;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

    private static final String TAG = "AssetAdapter";

    private Context context;
    private List<Asset> assetList;
    private ProgressDialog progressDialog;
    private QuantityChangeListener quantityListener;

    public interface QuantityChangeListener {
        void onQuantityChange(int assetId, int quantity);
    }

    public AssetAdapter(Context context, List<Asset> assetList, QuantityChangeListener listener) {
        this.context = context;
        this.assetList = assetList;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
        this.quantityListener = listener;
    }

    public void updateData(List<Asset> newList, int startIndex) {
        this.assetList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_asset, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        Asset asset = assetList.get(position);
        holder.textViewName.setText(asset.getNamaBarang());
        holder.textViewKodeBarang.setText("Kode: " + asset.getKodeBarang());
        holder.textViewJumlahStok.setText("Stok: " + asset.getJumlahStok());
        holder.textViewLokasiBarang.setText("Lokasi: " + asset.getLokasiBarang());
        holder.textViewCategory.setText("Jurusan: " + asset.getJurusanBarang());
        holder.textViewMerk.setText("Merk: " + (asset.getMerk() != null ? asset.getMerk() : "-"));
        holder.textViewSumber.setText("Sumber: " + (asset.getSumber() != null ? asset.getSumber() : "-"));
        holder.textViewTahun.setText("Tahun: " + (asset.getTahun() != null ? asset.getTahun() : "-"));
        holder.textViewDescription.setText("Deskripsi: " + (asset.getDeskripsi() != null ? asset.getDeskripsi() : "-"));
        holder.textViewValue.setText("Harga: Rp " + String.valueOf(asset.getHargaSatuan()));

        // Set initial quantity to 0
        holder.editTextQuantity.setText("0");
        int stock = Integer.parseInt(asset.getJumlahStok());

        // Quantity listeners
        holder.buttonDecrease.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.editTextQuantity.getText().toString());
            if (current > 0) {
                current--;
                holder.editTextQuantity.setText(String.valueOf(current));
                if (quantityListener != null) {
                    quantityListener.onQuantityChange(asset.getId(), current);
                }
            }
        });

        holder.buttonIncrease.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.editTextQuantity.getText().toString());
            if (current < stock) {
                current++;
                holder.editTextQuantity.setText(String.valueOf(current));
                if (quantityListener != null) {
                    quantityListener.onQuantityChange(asset.getId(), current);
                }
            }
        });

        // Add TextWatcher to validate manual input
        holder.editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int qty = Integer.parseInt(s.toString());
                    if (qty > stock) {
                        qty = stock;
                        holder.editTextQuantity.setText(String.valueOf(qty));
                        holder.editTextQuantity.setSelection(String.valueOf(qty).length());
                    } else if (qty < 0) {
                        qty = 0;
                        holder.editTextQuantity.setText("0");
                        holder.editTextQuantity.setSelection(1);
                    }
                    if (quantityListener != null) {
                        quantityListener.onQuantityChange(asset.getId(), qty);
                    }
                } catch (NumberFormatException e) {
                    holder.editTextQuantity.setText("0");
                    if (quantityListener != null) {
                        quantityListener.onQuantityChange(asset.getId(), 0);
                    }
                }
            }
        });

        holder.editTextQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int qty = Integer.parseInt(holder.editTextQuantity.getText().toString());
                    if (qty > stock) {
                        qty = stock;
                        holder.editTextQuantity.setText(String.valueOf(qty));
                    } else if (qty < 0) {
                        qty = 0;
                        holder.editTextQuantity.setText("0");
                    }
                    if (quantityListener != null) {
                        quantityListener.onQuantityChange(asset.getId(), qty);
                    }
                } catch (NumberFormatException e) {
                    holder.editTextQuantity.setText("0");
                    if (quantityListener != null) {
                        quantityListener.onQuantityChange(asset.getId(), 0);
                    }
                }
            }
        });

        // For admin/officer, show edit/delete; for student, hide or disable
        // Assume role check in activity, here just set visibility if needed
        if (SharedPrefManager.getInstance(context).getUserRole().equals("students")) {
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
        } else {
            holder.buttonEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddAssetActivity.class);
                intent.putExtra("asset_id", asset.getId());
                intent.putExtra("asset_nama_barang", asset.getNamaBarang());
                intent.putExtra("asset_kode_barang", asset.getKodeBarang());
                intent.putExtra("asset_jumlah_stok", asset.getJumlahStok());
                intent.putExtra("asset_lokasi_barang", asset.getLokasiBarang());
                intent.putExtra("asset_jurusan_barang", asset.getJurusanBarang());
                intent.putExtra("asset_merk", asset.getMerk());
                intent.putExtra("asset_harga_satuan", String.valueOf(asset.getHargaSatuan()));
                intent.putExtra("asset_sumber", asset.getSumber());
                intent.putExtra("asset_tahun", asset.getTahun());
                intent.putExtra("asset_deskripsi", asset.getDeskripsi());
                context.startActivity(intent);
            });

            holder.buttonDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus Aset")
                        .setMessage("Apakah Anda yakin ingin menghapus aset ini?")
                        .setPositiveButton("Ya", (dialog, which) -> deleteAsset(asset.getId(), position))
                        .setNegativeButton("Tidak", null)
                        .show();
            });
        }

        // Stock status styling
        int stokInt = Integer.parseInt(asset.getJumlahStok());
        if (stokInt == 0) {
            holder.textViewJumlahStok.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else if (stokInt <= 5) {
            holder.textViewJumlahStok.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Load image if available
        String photoUrl = asset.getPhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(context)
                .load(photoUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imageViewPhoto);
            holder.imageViewPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    private void deleteAsset(int assetId, int position) {
        progressDialog.setMessage("Deleting asset...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                Constants.URL_DELETE_ASSET + "/" + assetId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "DELETE_RESPONSE: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getBoolean("success")) {
                                ((AssetListActivity) context).loadAssets();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parse error", e);
                            Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Volley error", error);
                        Toast.makeText(context, "Error deleting asset", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPrefManager.getInstance(context).getToken();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static class AssetViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewKodeBarang, textViewJumlahStok, textViewLokasiBarang,
                textViewCategory, textViewMerk, textViewSumber, textViewTahun,
                textViewDescription, textViewValue;
        Button buttonEdit, buttonDelete, buttonDecrease, buttonIncrease;
        EditText editTextQuantity;
        ImageView imageViewPhoto;

        public AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewKodeBarang = itemView.findViewById(R.id.textViewKodeBarang);
            textViewJumlahStok = itemView.findViewById(R.id.textViewJumlahStok);
            textViewLokasiBarang = itemView.findViewById(R.id.textViewLokasiBarang);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewMerk = itemView.findViewById(R.id.textViewMerk);
            textViewSumber = itemView.findViewById(R.id.textViewSumber);
            textViewTahun = itemView.findViewById(R.id.textViewTahun);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewValue = itemView.findViewById(R.id.textViewValue);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            editTextQuantity = itemView.findViewById(R.id.editTextQuantity);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}
