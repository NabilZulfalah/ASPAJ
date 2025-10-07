package com.example.androidphpmysql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class KelasAdapter extends RecyclerView.Adapter<KelasAdapter.KelasViewHolder> {

    public interface OnKelasActionListener {
        void onEdit(Kelas kelas);
        void onDelete(Kelas kelas);
    }

    private Context context;
    private List<Kelas> kelasList;
    private OnKelasActionListener listener;
    private int startIndex = 0;

    public KelasAdapter(Context context, List<Kelas> kelasList, OnKelasActionListener listener, int startIndex) {
        this.context = context;
        this.kelasList = kelasList;
        this.listener = listener;
        this.startIndex = startIndex;
    }

    public void updateData(List<Kelas> newList, int newStartIndex) {
        this.kelasList = newList;
        this.startIndex = newStartIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KelasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kelas, parent, false);
        return new KelasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelasViewHolder holder, int position) {
        Kelas kelas = kelasList.get(position);
        holder.textViewNo.setText(String.valueOf(startIndex + position + 1));
        holder.textViewNamaKelas.setText(kelas.getName());
        holder.textViewLevel.setText(kelas.getLevel());
        holder.textViewProgramStudi.setText(kelas.getProgramStudy());
        holder.textViewDescription.setText(kelas.getDescription());

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(kelas);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(kelas);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kelasList.size();
    }

    public static class KelasViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNo, textViewNamaKelas, textViewLevel, textViewProgramStudi, textViewDescription;
        ImageButton buttonEdit, buttonDelete;

        public KelasViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNo = itemView.findViewById(R.id.textview_no);
            textViewNamaKelas = itemView.findViewById(R.id.textview_nama_kelas);
            textViewLevel = itemView.findViewById(R.id.textview_level);
            textViewProgramStudi = itemView.findViewById(R.id.textview_program_studi);
            textViewDescription = itemView.findViewById(R.id.textview_description);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
