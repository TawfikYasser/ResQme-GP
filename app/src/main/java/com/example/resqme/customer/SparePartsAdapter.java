package com.example.resqme.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartInCart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.SparePartsViewHolder> {


    Context context;
    ArrayList<SparePart> spareParts;
    String c_userid = "";
    DatabaseReference spDB;
    public SparePartsAdapter(Context context, ArrayList<SparePart> spareParts) {
        this.context = context;
        this.spareParts = spareParts;
        SharedPreferences userData = this.context.getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        c_userid = userData.getString("C_USERID", "C_DEFAULT");
        spDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
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
        holder.itemPrice.setText(sparePart.getItemPrice() + " جنيه");
        if(position == 0){
            holder.masked.setVisibility(View.VISIBLE);
        }else{
            holder.masked.setVisibility(View.GONE);
        }

        spDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                    if(serviceProvider.getUserId().equals(sparePart.getItemServiceProviderId())){
                        holder.itemOwnerRate.setText(new DecimalFormat("##.##").format(Double.valueOf(serviceProvider.getRate())) + " / 5");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ShoppingCart");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(sparePart.getItemID()+"-CCC-"+c_userid).exists()) {
                    holder.addToCart.setEnabled(false);
                }else{
                    holder.addToCart.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
                LogData.saveLog("SERVICE_CLICK",sparePart.getItemID(),"SPARE_PARTS","", "SPARE_PARTS");
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ShoppingCart");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.child(sparePart.getItemID()+"-CCC-"+c_userid).exists()) {
                            Toast.makeText(context, "موجود بالفعل", Toast.LENGTH_SHORT).show();
                        }else{
                            SparePartInCart sparePartObj = new SparePartInCart(sparePart.getItemID()+"-CCC-"+c_userid, sparePart.getItemID(), c_userid, sparePart.getItemName(), sparePart.getItemImage(), sparePart.getItemPrice(),
                                    sparePart.getItemNewOrUsed(), sparePart.getItemStatus(), sparePart.getItemServiceProviderId(),
                                    sparePart.getItemCarType(), sparePart.getItemAvailability());
                            reference.child(sparePart.getItemID()+"-CCC-"+c_userid).setValue(sparePartObj);
                            Toast.makeText(context,"تم إضافة: "+ sparePart.getItemName(), Toast.LENGTH_SHORT).show();
                            LogData.saveLog("SERVICE_CLICK",sparePart.getItemID(),"SPARE_PARTS","", "SPARE_PARTS");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return spareParts.size();
    }

    public class SparePartsViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemPrice, itemOwnerRate;
        MaterialButton addToCart;
        LinearLayout masked;
        public SparePartsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.spare_parts_item_image);
            itemName = itemView.findViewById(R.id.spare_parts_item_name);
            itemPrice = itemView.findViewById(R.id.spare_parts_item_price);
            addToCart = itemView.findViewById(R.id.add_item_to_cart_spare_parts_item_adapter);
            itemOwnerRate = itemView.findViewById(R.id.spare_parts_item_sp_rate);
            masked = itemView.findViewById(R.id.most_asked_layout_spare_parts);
        }
    }
}
