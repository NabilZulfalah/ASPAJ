package com.example.androidphpmysql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.CommodityViewHolder> {

    private Context mCtx;
    private List<Commodity> commodityList;

    public CommodityAdapter(Context mCtx, List<Commodity> commodityList) {
        this.mCtx = mCtx;
        this.commodityList = commodityList;
    }

    @NonNull
    @Override
    public CommodityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item_commodity, null);
        return new CommodityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommodityViewHolder holder, int position) {
        Commodity commodity = commodityList.get(position);

        holder.textViewCommodityName.setText(commodity.getName());
        holder.textViewCommodityStock.setText("Stock: " + commodity.getStock());
        holder.textViewCommodityLocation.setText("Location: " + commodity.getLokasi());
    }

    @Override
    public int getItemCount() {
        return commodityList.size();
    }

    class CommodityViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCommodityName, textViewCommodityStock, textViewCommodityLocation;

        public CommodityViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCommodityName = itemView.findViewById(R.id.textViewCommodityName);
            textViewCommodityStock = itemView.findViewById(R.id.textViewCommodityStock);
            textViewCommodityLocation = itemView.findViewById(R.id.textViewCommodityLocation);
        }
    }
}
