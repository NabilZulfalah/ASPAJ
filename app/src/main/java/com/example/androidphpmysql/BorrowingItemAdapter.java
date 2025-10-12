package com.example.androidphpmysql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BorrowingItemAdapter extends RecyclerView.Adapter<BorrowingItemAdapter.ViewHolder> {

    private Context context;
    private List<BorrowingItem> itemList;
    private int borrowingId;

    public BorrowingItemAdapter(Context context, List<BorrowingItem> itemList, int borrowingId) {
        this.context = context;
        this.itemList = itemList;
        this.borrowingId = borrowingId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowingItem item = itemList.get(position);

        holder.tvCode.setText(item.getCode());
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText("Qty " + item.getQuantity());
        holder.tvStatus.setText(item.getStatus().toUpperCase());

        // Load photo if available
        if (item.getPhotoUrl() != null && !item.getPhotoUrl().isEmpty()) {
            Glide.with(context).load(item.getPhotoUrl()).into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_asset_placeholder);
        }

        // Set status color
        int statusColor = context.getResources().getColor(android.R.color.darker_gray);
        switch (item.getStatus().toLowerCase()) {
            case "returned":
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "rejected":
                statusColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "approved":
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                break;
            case "borrowed":
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
        }
        holder.tvStatus.setBackgroundColor(statusColor);
        holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.white));

        if (item.getStockInfo() != null && !item.getStockInfo().isEmpty()) {
            holder.tvStockInfo.setVisibility(View.VISIBLE);
            holder.tvStockInfo.setText(item.getStockInfo());
        } else {
            holder.tvStockInfo.setVisibility(View.GONE);
        }

        // For borrowed status, show return button
        if ("borrowed".equals(item.getStatus())) {
            holder.btnReturnItem.setVisibility(View.VISIBLE);
            holder.btnReturnItem.setOnClickListener(v -> {
                Intent intent = new Intent(context, ReturnFormActivity.class);
                intent.putExtra("borrowing_id", String.valueOf(borrowingId));
                intent.putExtra("item_id", String.valueOf(item.getId()));
                ((android.app.Activity) context).startActivityForResult(intent, 1); // Request code 1 for return
            });
        } else {
            holder.btnReturnItem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView tvCode, tvName, tvStatus, tvQuantity, tvStockInfo;
        Button btnReturnItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage); // Add ImageView to layout if needed
            tvCode = itemView.findViewById(R.id.tvCode);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvStockInfo = itemView.findViewById(R.id.tvStockInfo);
            btnReturnItem = itemView.findViewById(R.id.btnReturnItem);
        }
    }
}
