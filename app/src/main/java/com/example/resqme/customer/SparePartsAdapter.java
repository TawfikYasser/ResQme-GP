package com.example.resqme.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.model.SparePart;
import com.google.android.material.button.MaterialButton;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailsOfSparePart = new Intent(context, SparePartsDetails.class);
                goToDetailsOfSparePart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goToDetailsOfSparePart.putExtra("ITEM_ID",sparePart.getItemID());
                goToDetailsOfSparePart.putExtra("ITEM_NAME",sparePart.getItemName());
                goToDetailsOfSparePart.putExtra("ITEM_AVAILABILITY",sparePart.getItemAvailability());
                goToDetailsOfSparePart.putExtra("ITEM_PRICE",sparePart.getItemPrice());
                goToDetailsOfSparePart.putExtra("ITEM_CARTYPE",sparePart.getItemCarType());
                goToDetailsOfSparePart.putExtra("ITEM_IMAGE",sparePart.getItemImage());
                goToDetailsOfSparePart.putExtra("ITEM_OWNER_ID",sparePart.getItemServiceProviderId());
                goToDetailsOfSparePart.putExtra("ITEM_STATUS",sparePart.getItemStatus());
                goToDetailsOfSparePart.putExtra("ITEM_NEWORUSED",sparePart.getItemNewOrUsed());
                context.startActivity(goToDetailsOfSparePart);
            }
        });
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Need to handle duplicates, and save data to shared.
//                Toast.makeText(context, CustomerHome.customerCart.getSparePartArrayList().size() + "", Toast.LENGTH_SHORT).show();
                CustomerHome.customerCart.setSparePartArrayList(new SparePart(
                        sparePart.getItemID(), sparePart.getItemName(), sparePart.getItemImage(), sparePart.getItemImage(),
                        sparePart.getItemNewOrUsed(), sparePart.getItemStatus(), sparePart.getItemServiceProviderId(),
                        sparePart.getItemCarType(), sparePart.getItemAvailability()
                ));

            }
        });
    }

    @Override
    public int getItemCount() {
        return spareParts.size();
    }

    public class SparePartsViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice, itemNewOrUsed, itemCarType;
        MaterialButton addToCart;
        public SparePartsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.spare_parts_item_image);
            itemName = itemView.findViewById(R.id.spare_parts_item_name);
            itemPrice = itemView.findViewById(R.id.spare_parts_item_price);
            itemNewOrUsed = itemView.findViewById(R.id.spare_parts_item_usednew);
            itemCarType = itemView.findViewById(R.id.spare_parts_item_cartype);
            addToCart = itemView.findViewById(R.id.add_item_to_cart_spare_parts_item_adapter);
        }
    }
}
