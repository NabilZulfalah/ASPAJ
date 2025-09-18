package com.example.androidphpmysql.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.models.Borrowing;

import java.util.List;

public class PendingBorrowingsAdapter extends RecyclerView.Adapter<PendingBorrowingsAdapter.ViewHolder> {

    private List<Borrowing> pendingBorrowingsList;
    private OnItemClickListener listener;

    // Constructor
    public PendingBorrowingsAdapter(List<Borrowing> pendingBorrowingsList) {
        this.pendingBorrowingsList = pendingBorrowingsList;
    }

    // Interface untuk klik item
    public interface OnItemClickListener {
        void onItemClick(Borrowing borrowing);
    }

    // Setter listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Update data list
    public void setData(List<Borrowing> newList) {
        this.pendingBorrowingsList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingBorrowingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_borrowing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingBorrowingsAdapter.ViewHolder holder, int position) {
        Borrowing borrowing = pendingBorrowingsList.get(position);
        holder.textViewStudentName.setText(borrowing.getStudentName());
        holder.textViewBorrowDate.setText(borrowing.getBorrowDate());
        holder.textViewReturnDate.setText(borrowing.getReturnDate());
    }

    @Override
    public int getItemCount() {
        return pendingBorrowingsList != null ? pendingBorrowingsList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStudentName, textViewBorrowDate, textViewReturnDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewBorrowDate = itemView.findViewById(R.id.textViewBorrowDate);
            textViewReturnDate = itemView.findViewById(R.id.textViewReturnDate);

            // Handle klik item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pendingBorrowingsList.get(position));
                    }
                }
            });
        }
    }
}
