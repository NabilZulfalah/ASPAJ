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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BorrowingCardAdapter extends RecyclerView.Adapter<BorrowingCardAdapter.ViewHolder> {

    private Context context;
    private List<Borrowing> borrowingList;

    public BorrowingCardAdapter(Context context, List<Borrowing> borrowingList) {
        this.context = context;
        this.borrowingList = borrowingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrowing_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrowing borrowing = borrowingList.get(position);

        try {
            // Get first item for summary
            JSONArray items = borrowing.getItems();
            if (items.length() > 0) {
                JSONObject firstItem = items.getJSONObject(0);
                String assetName = firstItem.getJSONObject("commodity").getString("name");
                int quantity = firstItem.getInt("quantity");
                holder.assetName.setText(assetName + " (" + quantity + " unit)");

                // Load photo if available
                String photoUrl = firstItem.getJSONObject("commodity").optString("photo", null);
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(context).load(photoUrl).into(holder.assetImage);
                } else {
                    holder.assetImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            holder.tujuan.setText(borrowing.getTujuan());
            holder.borrowDate.setText("Tanggal Peminjaman: " + borrowing.getBorrowDate());
            holder.returnDate.setText("Tanggal Kembali: " + borrowing.getReturnDate());
            holder.status.setText(borrowing.getStatus().toUpperCase());

            // Set status color (simple, can enhance with drawables)
            if ("pending".equals(borrowing.getStatus())) {
                holder.status.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
            } else if ("approved".equals(borrowing.getStatus())) {
                holder.status.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
            } else if ("returned".equals(borrowing.getStatus())) {
                holder.status.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            }

            holder.detailButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, BorrowingDetailActivity.class);
                intent.putExtra("borrowing_id", borrowing.getId());
                context.startActivity(intent);
            });

            // Card click for detail
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, BorrowingDetailActivity.class);
                intent.putExtra("borrowing_id", borrowing.getId());
                context.startActivity(intent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return borrowingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView assetImage;
        TextView assetName, tujuan, borrowDate, returnDate, status;
        Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assetImage = itemView.findViewById(R.id.assetImage);
            assetName = itemView.findViewById(R.id.assetName);
            tujuan = itemView.findViewById(R.id.tujuan);
            borrowDate = itemView.findViewById(R.id.borrowDate);
            returnDate = itemView.findViewById(R.id.returnDate);
            status = itemView.findViewById(R.id.status);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
}
