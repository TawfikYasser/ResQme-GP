package com.example.resqme.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.model.SparePart;

import java.util.ArrayList;

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.SparePartsViewHolder> {


    Context context;
    ArrayList<SparePart> spareParts;

    public SparePartsAdapter(Context context, ArrayList<SparePart> spareParts) {
        this.context = context;
        this.spareParts = spareParts;
    }

    @NonNull
    @Override
    public SparePartsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_parts_item_customer_home, parent, false);
        return new SparePartsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SparePartsViewHolder holder, int position) {
    SparePart sparePart = spareParts.get(position);
    Glide.with(context).load(sparePart.getItemImage()).into(holder.itemImage);
    holder.itemName.setText(sparePart.getItemName());
    holder.itemPrice.setText(sparePart.getItemPrice());
    holder.itemNewOrUsed.setText(sparePart.getItemNewOrUsed());
    holder.itemCarType.setText(sparePart.getItemCarType());

    }

    @Override
    public int getItemCount() {
        return spareParts.size();
    }

    public class SparePartsViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice, itemNewOrUsed, itemCarType;

        public SparePartsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.spare_parts_item_image);
            itemName = itemView.findViewById(R.id.spare_parts_item_name);
            itemPrice = itemView.findViewById(R.id.spare_parts_item_price);
            itemNewOrUsed = itemView.findViewById(R.id.spare_parts_item_usednew);
            itemCarType = itemView.findViewById(R.id.spare_parts_item_cartype);
        }
    }
}
