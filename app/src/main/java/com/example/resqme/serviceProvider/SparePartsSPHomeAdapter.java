package com.example.resqme.serviceProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.resqme.customer.SparePartsAdapter;
import com.example.resqme.customer.SparePartsDetails;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartInCart;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SparePartsSPHomeAdapter extends RecyclerView.Adapter<SparePartsSPHomeAdapter.SparePartsSPHomeViewHolder> {
    Context context;
    ArrayList<SparePart> spareParts;
    String c_userid = "";

    public SparePartsSPHomeAdapter(Context context, ArrayList<SparePart> spareParts) {
        this.context = context;
        this.spareParts = spareParts;
        SharedPreferences userData = this.context.getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        c_userid = userData.getString("C_USERID", "C_DEFAULT");
    }

    @NonNull
    @Override
    public SparePartsSPHomeAdapter.SparePartsSPHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_parts_item_sp_home, parent, false);
        return new SparePartsSPHomeAdapter.SparePartsSPHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SparePartsSPHomeAdapter.SparePartsSPHomeViewHolder holder, int position) {
        SparePart sparePart = spareParts.get(position);
        Glide.with(context).load(sparePart.getItemImage()).into(holder.itemImage);
        holder.itemName.setText(sparePart.getItemName());
        holder.itemPrice.setText(sparePart.getItemPrice() + " جنيه");
        holder.itemCarBrand.setText(sparePart.getItemCarType());
        holder.itemNewUsed.setText(sparePart.getItemNewOrUsed());

        if(sparePart.getItemAvailability().equals("Available")){
            holder.itemAvailability.setText("متاح");
            holder.itemAvailability.setTextColor(Color.GREEN);
            holder.changeAvailability.setText("اجعل القطعة غير متاحة");
        }else{
            holder.itemAvailability.setText("غير متاح");
            holder.itemAvailability.setTextColor(Color.RED);
            holder.changeAvailability.setText("اجعل القطعة متاحة");
        }

        if(sparePart.getItemStatus().equals("Pending")){
            holder.changeAvailability.setEnabled(false);
            holder.itemAvailability.setText("غير متاح");
            holder.itemAvailability.setTextColor(Color.RED);

            holder.itemStatus.setText("يتم مراجعة بيانات القطعة");
            holder.itemStatus.setTextColor(Color.rgb(255, 166, 53));
        }else if(sparePart.getItemStatus().equals("Approved")){
            holder.changeAvailability.setEnabled(true);
            holder.itemStatus.setText("تم قبول قطعة الغيار");
            holder.itemStatus.setTextColor(Color.GREEN);
        }else if(sparePart.getItemStatus().equals("Refused")){
            holder.itemAvailability.setText("غير متاح");
            holder.itemAvailability.setTextColor(Color.RED);

            holder.changeAvailability.setEnabled(false);
            holder.itemStatus.setText("تم رفض الونش");
            holder.itemStatus.setTextColor(Color.RED);
        }

        holder.changeAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Change item availability
                if(sparePart.getItemAvailability().equals("Available")){
                    // Make it not available
                    DatabaseReference sparePartsTable = FirebaseDatabase.getInstance().getReference().child("SpareParts");
                    sparePartsTable.child(sparePart.getItemID()).child("itemAvailability").setValue("Not Available");
                }else{
                    // Make it available
                    DatabaseReference sparePartsTable = FirebaseDatabase.getInstance().getReference().child("SpareParts");
                    sparePartsTable.child(sparePart.getItemID()).child("itemAvailability").setValue("Available");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return spareParts.size();
    }

    public class SparePartsSPHomeViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice, itemStatus, itemAvailability, itemNewUsed, itemCarBrand;
        MaterialButton changeAvailability;
        public SparePartsSPHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.spare_parts_item_image_sp_home);
            itemName = itemView.findViewById(R.id.spare_parts_item_name_sp_home);
            itemPrice = itemView.findViewById(R.id.spare_parts_item_price_sp_home);
            itemStatus = itemView.findViewById(R.id.spare_parts_item_status_sp_home);
            itemAvailability = itemView.findViewById(R.id.spare_parts_item_availability_sp_home);
            itemNewUsed = itemView.findViewById(R.id.spare_parts_item_new_used_sp_home);
            itemCarBrand = itemView.findViewById(R.id.spare_parts_item_car_brand_sp_home);
            changeAvailability = itemView.findViewById(R.id.spare_parts_sp_home_change_availability_btn);
        }
    }
}
