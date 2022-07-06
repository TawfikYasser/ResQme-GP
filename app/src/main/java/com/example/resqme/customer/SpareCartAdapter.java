package com.example.resqme.customer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.SparePartInCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpareCartAdapter extends RecyclerView.Adapter<SpareCartAdapter.SpareCartViewHolder>{

    Context context;
    ArrayList<SparePartInCart> sparePartInCarts;
    DatabaseReference reference;

    public SpareCartAdapter(Context context, ArrayList<SparePartInCart> sparePartInCarts, DatabaseReference reference) {
        this.context = context;
        this.sparePartInCarts = sparePartInCarts;
        this.reference = reference;
    }

    @NonNull
    @Override
    public SpareCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_cart_item, parent, false);
        return new SpareCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpareCartViewHolder holder, int position) {
            SparePartInCart sparePartInCart = sparePartInCarts.get(position);
            Glide.with(context).load(sparePartInCart.getItemImage()).into(holder.itemImage);
            holder.itemName.setText(sparePartInCart.getItemName());
            holder.itemPrice.setText(sparePartInCart.getItemPrice());
            holder.itemNewOrUsed.setText(sparePartInCart.getItemNewOrUsed());
            holder.itemCarType.setText(sparePartInCart.getItemCarType());
            if(sparePartInCart.getItemAvailability().equals("Available")){
                holder.itemAvailability.setText("متاح");
                holder.itemAvailability.setTextColor(Color.GREEN);
            }else{
                holder.itemAvailability.setText("غير متاح");
                holder.itemAvailability.setTextColor(Color.rgb(255, 166, 53));
            }
            holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reference.child(sparePartInCart.getItemInCartID()).removeValue();
                    LogData.saveLog("SERVICE_CLICK",sparePartInCart.getItemInCartID(),"SPARE_PARTS","", "CART_REMOVE");
                    LogData.saveLog("APP_CLICK","","","CLICK ON REMOVE ITEM FROM CART", "CART");
                }
            });
    }

    @Override
    public int getItemCount() {
        return sparePartInCarts.size();
    }

    class SpareCartViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice, itemNewOrUsed, itemCarType, itemAvailability;
        MaterialButton removeFromCart;
        public SpareCartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.spare_cart_item_image);
            itemName = itemView.findViewById(R.id.spare_cart_item_name_txt);
            itemPrice = itemView.findViewById(R.id.spare_cart_item_price_txt);
            itemNewOrUsed = itemView.findViewById(R.id.spare_cart_item_newused_txt);
            itemCarType = itemView.findViewById(R.id.spare_cart_item_cartype_txt);
            itemAvailability = itemView.findViewById(R.id.spare_cart_item_availability_txt);
            removeFromCart = itemView.findViewById(R.id.remove_from_cart_item_btn);
        }
    }
}
