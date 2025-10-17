package com.example.androidphpmysql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AdminBorrowingAdapter extends RecyclerView.Adapter<AdminBorrowingAdapter.ViewHolder> {

    private Context context;
    private List<Borrowing> borrowingList;
    private OnBorrowingActionListener listener;

public interface OnBorrowingActionListener {
    void onApprove(Borrowing borrowing);
    void onReject(Borrowing borrowing);
    void onReturn(Borrowing borrowing);
    void onViewPhoto(String photoUrl);
    void onViewReturnPhotos(Borrowing borrowing);
}

    public AdminBorrowingAdapter(Context context, List<Borrowing> borrowingList, OnBorrowingActionListener listener) {
        this.context = context;
        this.borrowingList = borrowingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_borrowing_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrowing borrowing = borrowingList.get(position);

        // Student info
        holder.textStudentName.setText(borrowing.getStudent().getName());
        String className = borrowing.getStudent().getSchoolClass() != null ? borrowing.getStudent().getSchoolClass().getName() : "Tidak ada kelas";
        holder.textStudentClass.setText(className);

        // Load student image if available
        if (borrowing.getStudent().getUser() != null && borrowing.getStudent().getUser().getProfilePhoto() != null) {
            Glide.with(context)
                    .load(borrowing.getStudent().getUser().getProfilePhoto())
                    .placeholder(R.drawable.ic_asset_placeholder)
                    .into(holder.imageStudent);
        }

        // Status
        holder.textStatus.setText(borrowing.getStatus());
        holder.textStatus.setBackgroundColor(getStatusColor(borrowing.getStatus()));

        // Details
        holder.textPurpose.setText("Tujuan: " + borrowing.getTujuan());
        holder.textBorrowDate.setText("Tanggal Pinjam: " + borrowing.getBorrowDate());
        holder.textReturnDate.setText("Tanggal Kembali: " + borrowing.getReturnDate());

        // Items
        holder.layoutItems.removeAllViews();
        for (BorrowingItem item : borrowing.getItemsList()) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_borrowed_item, holder.layoutItems, false);
            TextView textItemName = itemView.findViewById(R.id.tvName);
            TextView textItemQuantity = itemView.findViewById(R.id.tvQuantity);
            TextView textItemStatus = itemView.findViewById(R.id.tvStatus);
            ImageView imageItemPhoto = itemView.findViewById(R.id.itemImage);

            textItemName.setText(item.getCommodity().getName());
            textItemQuantity.setText("Qty: " + item.getQuantity());
            textItemStatus.setText(item.getStatus());

            if (item.getPhotoUrl() != null) {
                Glide.with(context)
                        .load(item.getPhotoUrl())
                        .placeholder(R.drawable.ic_asset_placeholder)
                        .into(imageItemPhoto);
                imageItemPhoto.setOnClickListener(v -> listener.onViewPhoto(item.getPhotoUrl()));
            }

            holder.layoutItems.addView(itemView);
        }

        // Actions
        holder.buttonApprove.setOnClickListener(v -> {
            listener.onApprove(borrowing);
        });

        holder.buttonReject.setOnClickListener(v -> {
            listener.onReject(borrowing);
        });

        holder.buttonReturn.setOnClickListener(v -> listener.onReturn(borrowing));

        holder.buttonViewReturnPhoto.setOnClickListener(v -> listener.onViewReturnPhotos(borrowing));

        // Show/hide buttons based on status
        boolean hasPendingItems = borrowing.getItemsList().stream().anyMatch(item -> "pending".equals(item.getStatus()));
        String borrowingStatus = borrowing.getStatus();
        boolean hasApprovedItems = borrowing.getItemsList().stream().anyMatch(item -> "approved".equals(item.getStatus()));
        boolean canReturn = hasApprovedItems && ("approved".equals(borrowingStatus) || "partially_approved".equals(borrowingStatus) || "partially_returned".equals(borrowingStatus));
        boolean hasReturnPhotos = borrowing.getReturnPhoto() != null && borrowing.getReturnPhoto().length() > 0 && ("returned".equals(borrowingStatus) || "partially_returned".equals(borrowingStatus));

        holder.buttonApprove.setVisibility(hasPendingItems ? View.VISIBLE : View.GONE);
        holder.buttonReject.setVisibility(hasPendingItems ? View.VISIBLE : View.GONE);
        holder.buttonReturn.setVisibility(canReturn ? View.VISIBLE : View.GONE);
        holder.buttonViewReturnPhoto.setVisibility(hasReturnPhotos ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return borrowingList.size();
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending": return 0xFFFF9800; // Orange
            case "approved": return 0xFF4CAF50; // Green
            case "rejected": return 0xFFF44336; // Red
            case "returned": return 0xFF2196F3; // Blue
            default: return 0xFF9E9E9E; // Grey
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageStudent;
        TextView textStudentName, textStudentClass, textStatus, textPurpose, textBorrowDate, textReturnDate;
        LinearLayout layoutItems;
        Button buttonApprove, buttonReject, buttonReturn, buttonViewReturnPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStudent = itemView.findViewById(R.id.imageStudent);
            textStudentName = itemView.findViewById(R.id.textStudentName);
            textStudentClass = itemView.findViewById(R.id.textStudentClass);
            textStatus = itemView.findViewById(R.id.textStatus);
            textPurpose = itemView.findViewById(R.id.textPurpose);
            textBorrowDate = itemView.findViewById(R.id.textBorrowDate);
            textReturnDate = itemView.findViewById(R.id.textReturnDate);
            layoutItems = itemView.findViewById(R.id.layoutItems);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
            buttonReturn = itemView.findViewById(R.id.buttonReturn);
            buttonViewReturnPhoto = itemView.findViewById(R.id.buttonViewReturnPhoto);
        }
    }
}
