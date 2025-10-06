package com.example.androidphpmysql.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.models.Borrowing;

import java.util.List;

public class PendingBorrowingsAdapter extends RecyclerView.Adapter<PendingBorrowingsAdapter.ViewHolder> {

    private Context context;
    private List<Borrowing> borrowingList;
    private OnApproveRejectListener listener;

    // Constructor
    public PendingBorrowingsAdapter(Context context, List<Borrowing> borrowingList) {
        this.context = context;
        this.borrowingList = borrowingList;
    }

    // Interface listener
    public interface OnApproveRejectListener {
        void onApproveClick(Borrowing borrowing, int position);
        void onRejectClick(Borrowing borrowing, int position);
    }

    public void setOnApproveRejectListener(OnApproveRejectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_borrowing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrowing borrowing = borrowingList.get(position);

        holder.textStudentName.setText(borrowing.getStudentName());
        holder.textTujuan.setText("Tujuan: " + borrowing.getTujuan());
        holder.textKelas.setText("Kelas: " + borrowing.getKelas());
        holder.textBorrowDate.setText("Pinjam: " + borrowing.getBorrowDate());
        holder.textReturnDate.setText("Kembali: " + borrowing.getReturnDate());

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApproveClick(borrowing, position);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onRejectClick(borrowing, position);
        });
    }

    @Override
    public int getItemCount() {
        return borrowingList.size();
    }

    public void removeItem(int position) {
        borrowingList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textStudentName, textTujuan, textKelas, textBorrowDate, textReturnDate;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudentName = itemView.findViewById(R.id.textStudentName);
            textTujuan = itemView.findViewById(R.id.textTujuan);
            textKelas = itemView.findViewById(R.id.textKelas);
            textBorrowDate = itemView.findViewById(R.id.textBorrowDate);
            textReturnDate = itemView.findViewById(R.id.textReturnDate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
