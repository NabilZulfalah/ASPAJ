package com.example.androidphpmysql;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class ReturnItemAdapter extends RecyclerView.Adapter<ReturnItemAdapter.ViewHolder> {

    private Activity context;
    private List<BorrowingDetailActivity.BorrowedItem> itemList;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int selectedPosition = -1;

    public ReturnItemAdapter(Activity context, List<BorrowingDetailActivity.BorrowedItem> itemList) {
        this.context = context;
        this.itemList = itemList;

        // Setup image picker launcher
        imagePickerLauncher = ((ReturnFormActivity) context).registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            try {
                                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                                if (selectedPosition != -1) {
                                    itemList.get(selectedPosition).setReturnPhoto(selectedImage);
                                    notifyItemChanged(selectedPosition);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_return_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowingDetailActivity.BorrowedItem item = itemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvCode.setText("Kode: " + item.getCode());
        holder.tvQty.setText("Qty: " + item.getQuantity());

        Bitmap returnPhoto = item.getReturnPhoto();
        if (returnPhoto != null) {
            holder.imageViewPhoto.setImageBitmap(returnPhoto);
        } else {
            holder.imageViewPhoto.setImageResource(android.R.color.darker_gray);
        }

        holder.buttonUploadPhoto.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            openImagePicker();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvQty;
        ImageView imageViewPhoto;
        Button buttonUploadPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemName);
            tvCode = itemView.findViewById(R.id.itemCode);
            tvQty = itemView.findViewById(R.id.itemQty);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
            buttonUploadPhoto = itemView.findViewById(R.id.buttonUploadPhoto);
        }
    }
}
