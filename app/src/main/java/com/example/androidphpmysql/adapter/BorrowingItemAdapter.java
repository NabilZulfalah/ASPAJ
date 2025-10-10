package com.example.androidphpmysql.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidphpmysql.R;
import com.example.androidphpmysql.model.BorrowingItem;
import java.util.List;

public class BorrowingItemAdapter extends RecyclerView.Adapter<BorrowingItemAdapter.ViewHolder> {
    private List<BorrowingItem> items;
    private Context context;
    private OnReturnClickListener onReturnClickListener;

    public interface OnReturnClickListener {
        void onReturnClick(String returnUrl, BorrowingItem item);
    }

    public BorrowingItemAdapter(List<BorrowingItem> items, Context context, OnReturnClickListener listener) {
        this.items = items;
        this.context = context;
        this.onReturnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowing_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowingItem item = items.get(position);
        holder.name.setText(item.getCommodity().getName());
        holder.code.setText("Kode: " + item.getCommodity().getCode());
        holder.status.setText(item.getStatus().toUpperCase());
        holder.qty.setText("Qty: " + item.getQuantity());

        // Lokasi dan info pengambilan
        if ("approved".equals(item.getStatus()) && item.getCommodity().getLokasi() != null) {
            holder.lokasi.setVisibility(View.VISIBLE);
            holder.lokasi.setText("Lokasi: " + item.getCommodity().getLokasi());
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setText("Silakan ambil barang di " + item.getCommodity().getLokasi() + " segera.");
        } else {
            holder.lokasi.setVisibility(View.GONE);
            holder.info.setVisibility(View.GONE);
        }

        // Tombol kembalikan
        if (item.isCanReturn()) {
            holder.returnBtn.setVisibility(View.VISIBLE);
            holder.returnBtn.setOnClickListener(v -> {
                if (onReturnClickListener != null) {
                    onReturnClickListener.onReturnClick(item.getReturnUrl(), item);
                }
            });
        } else {
            holder.returnBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, code, status, qty, lokasi, info;
        Button returnBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            code = itemView.findViewById(R.id.itemCode);
            status = itemView.findViewById(R.id.itemStatus);
            qty = itemView.findViewById(R.id.itemQty);
            lokasi = itemView.findViewById(R.id.itemLokasi);
            info = itemView.findViewById(R.id.itemInfo);
            returnBtn = itemView.findViewById(R.id.itemReturnBtn);
        }
    }
}
