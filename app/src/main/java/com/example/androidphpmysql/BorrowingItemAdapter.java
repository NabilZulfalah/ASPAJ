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
    private List<BorrowingDetailActivity.BorrowingItem> itemList;
    private int borrowingId;

    public BorrowingItemAdapter(Context context, List<BorrowingDetailActivity.BorrowingItem> itemList, int borrowingId) {
        this.context = context;
        this.itemList = itemList;
        this.borrowingId = borrowingId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowingDetailActivity.BorrowingItem item = itemList.get(position);

        holder.itemName.setText(item.getName());
        holder.itemCode.setText("Kode: " + item.getCode());
        holder.itemQuantity.setText("Qty: " + item.getQuantity());
        holder.itemStatus.setText(item.getStatus().toUpperCase());

        // Load photo if available
        if (item.getPhotoUrl() != null && !item.getPhotoUrl().isEmpty()) {
            Glide.with(context).load(item.getPhotoUrl()).into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Set status color
        if ("borrowed".equals(item.getStatus())) {
            holder.itemStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            holder.pickupNote.setVisibility(View.VISIBLE);
            holder.returnButton.setVisibility(View.VISIBLE);
            holder.returnButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ReturnFormActivity.class);
                intent.putExtra("borrowing_id", borrowingId);
                intent.putExtra("item_id", item.getId());
                context.startActivity(intent);
            });
        } else if ("returned".equals(item.getStatus())) {
            holder.itemStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
            holder.pickupNote.setVisibility(View.GONE);
            holder.returnButton.setVisibility(View.GONE);
        } else {
            holder.itemStatus.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.pickupNote.setVisibility(View.GONE);
            holder.returnButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemCode, itemQuantity, itemStatus, pickupNote;
        Button returnButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemCode = itemView.findViewById(R.id.itemCode);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            pickupNote = itemView.findViewById(R.id.pickupNote);
            returnButton = itemView.findViewById(R.id.returnButton);
        }
    }
}
