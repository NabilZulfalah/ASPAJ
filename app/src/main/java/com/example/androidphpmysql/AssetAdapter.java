package com.example.androidphpmysql;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    public AssetAdapter(Context context, List<Asset> assetList) {
        this.context = context;
        this.assetList = assetList;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setCancelable(false);
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
        holder.textViewKodeBarang.setText(asset.getKodeBarang());
        holder.textViewJumlahStok.setText(asset.getJumlahStok());
        holder.textViewLokasiBarang.setText(asset.getLokasiBarang());
        holder.textViewCategory.setText(asset.getJurusanBarang());
        holder.textViewMerk.setText(asset.getMerk());
        holder.textViewSumber.setText(asset.getSumber());
        holder.textViewTahun.setText(asset.getTahun());
        holder.textViewDescription.setText(asset.getDeskripsi());
        holder.textViewValue.setText(String.valueOf(asset.getHargaSatuan()));

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

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    private void deleteAsset(int assetId, int position) {
        progressDialog.setMessage("Deleting asset...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_ASSET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "DELETE_RESPONSE: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (jsonObject.getBoolean("success")) {
                                assetList.remove(position);
                                notifyItemRemoved(position);
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
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(assetId));
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static class AssetViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewKodeBarang, textViewJumlahStok, textViewLokasiBarang,
                textViewCategory, textViewMerk, textViewSumber, textViewTahun,
                textViewDescription, textViewValue;
        Button buttonEdit, buttonDelete;

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
        }
    }
}
