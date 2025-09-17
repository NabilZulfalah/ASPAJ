package com.example.androidphpmysql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RiwayatPeminjamanAdapter extends RecyclerView.Adapter<RiwayatPeminjamanAdapter.ViewHolder> {

    private final List<RiwayatPeminjaman.Borrowing> borrowingList;

    public RiwayatPeminjamanAdapter(List<RiwayatPeminjaman.Borrowing> borrowingList) {
        this.borrowingList = borrowingList;
    }

    @NonNull
    @Override
    public RiwayatPeminjamanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat_peminjaman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatPeminjamanAdapter.ViewHolder holder, int position) {
        RiwayatPeminjaman.Borrowing borrowing = borrowingList.get(position);

        holder.tvNo.setText(String.valueOf(borrowing.no));
        // Load profile photo with placeholder only
        if (!borrowing.fotoProfile.isEmpty()) {
            // Image loading library not available, skip loading image from URL
            // You can implement image loading later or use a placeholder
            holder.ivFotoProfile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.tvNamaMurid.setText(borrowing.namaMurid);
        holder.tvKelas.setText(borrowing.kelas);
        holder.tvBarangJumlah.setText(borrowing.barangJumlah);
        holder.tvTujuan.setText(borrowing.tujuan);
        holder.tvTanggalJam.setText(borrowing.tanggalJam);
        holder.tvStatus.setText(borrowing.status);
        holder.tvKondisiPengembalian.setText(borrowing.kondisiPengembalian);
        holder.tvDikembalikanOleh.setText(borrowing.dikembalikanOleh);

        // Load return photo or placeholder
        if (!borrowing.photo.isEmpty()) {
            // Image loading library not available, skip loading image from URL
            holder.ivPhoto.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.btnAksi.setText(borrowing.aksi);
        // You can add click listener for btnAksi if needed
    }

    @Override
    public int getItemCount() {
        return borrowingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo, tvNamaMurid, tvKelas, tvBarangJumlah, tvTujuan, tvTanggalJam, tvStatus, tvKondisiPengembalian, tvDikembalikanOleh;
        ImageView ivFotoProfile, ivPhoto;
        Button btnAksi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNo = itemView.findViewById(R.id.tvNo);
            ivFotoProfile = itemView.findViewById(R.id.ivFotoProfile);
            tvNamaMurid = itemView.findViewById(R.id.tvNamaMurid);
            tvKelas = itemView.findViewById(R.id.tvKelas);
            tvBarangJumlah = itemView.findViewById(R.id.tvBarangJumlah);
            tvTujuan = itemView.findViewById(R.id.tvTujuan);
            tvTanggalJam = itemView.findViewById(R.id.tvTanggalJam);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvKondisiPengembalian = itemView.findViewById(R.id.tvKondisiPengembalian);
            tvDikembalikanOleh = itemView.findViewById(R.id.tvDikembalikanOleh);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            btnAksi = itemView.findViewById(R.id.btnAksi);
        }
    }
}
