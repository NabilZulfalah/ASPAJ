package com.example.androidphpmysql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SchoolClassAdapter extends RecyclerView.Adapter<SchoolClassAdapter.SchoolClassViewHolder> {

    private Context mCtx;
    private List<SchoolClass> schoolClassList;
    private OnSchoolClassActionListener mListener;

    public interface OnSchoolClassActionListener {
        void onEdit(SchoolClass schoolClass);
        void onDelete(SchoolClass schoolClass);
    }

    public SchoolClassAdapter(Context mCtx, List<SchoolClass> schoolClassList, OnSchoolClassActionListener listener) {
        this.mCtx = mCtx;
        this.schoolClassList = schoolClassList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SchoolClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item_school_class, null);
        return new SchoolClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolClassViewHolder holder, int position) {
        SchoolClass schoolClass = schoolClassList.get(position);

        holder.textViewClassName.setText(schoolClass.getName());
        holder.textViewClassLevel.setText("Level: " + schoolClass.getLevel());
        holder.textViewProgramStudy.setText("Program: " + schoolClass.getProgramStudy());

        holder.buttonEdit.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onEdit(schoolClass);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDelete(schoolClass);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schoolClassList.size();
    }

    class SchoolClassViewHolder extends RecyclerView.ViewHolder {

        TextView textViewClassName, textViewClassLevel, textViewProgramStudy;
        Button buttonEdit, buttonDelete;

        public SchoolClassViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewClassName = itemView.findViewById(R.id.textViewClassName);
            textViewClassLevel = itemView.findViewById(R.id.textViewClassLevel);
            textViewProgramStudy = itemView.findViewById(R.id.textViewProgramStudy);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
