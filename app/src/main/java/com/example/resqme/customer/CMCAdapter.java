package com.example.resqme.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.CMC;
import com.example.resqme.model.ServiceProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CMCAdapter extends RecyclerView.Adapter<CMCAdapter.CMCViewHolder> {

    Context context;
    ArrayList<CMC> cmcs;
    DatabaseReference spDB;
    public CMCAdapter(Context context, ArrayList<CMC> cmcs) {
        this.context = context;
        this.cmcs = cmcs;
        spDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
    }

    @NonNull
    @Override
    public CMCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cmc_item_customer_home, parent, false);
        return new CMCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CMCViewHolder holder, int position) {
        CMC cmc = cmcs.get(position);
        Glide.with(context).load(cmc.getCmcImage()).into(holder.cmcImage);
        holder.cmcName.setText(cmc.getCmcName());
        holder.cmcLocation.setText(cmc.getCmcLocation());

        if(position == 0){
            holder.masked.setVisibility(View.VISIBLE);
        }

        spDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                    if(serviceProvider.getUserId().equals(cmc.getCmcServiceProviderId())){
                        holder.cmcOwnerRate.setText(new DecimalFormat("##.##").format(Double.valueOf(serviceProvider.getRate())) + " / 5");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailsOfCMC = new Intent(context, CMCDetails.class);
                goToDetailsOfCMC.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goToDetailsOfCMC.putExtra("CMC_ID",cmc.getCmcID());
                goToDetailsOfCMC.putExtra("CMC_NAME",cmc.getCmcName());
                goToDetailsOfCMC.putExtra("CMC_AVAILABILITY",cmc.getCmcAvailability());
                goToDetailsOfCMC.putExtra("CMC_OWNER_ID",cmc.getCmcServiceProviderId());
                goToDetailsOfCMC.putExtra("CMC_CARTYPE",cmc.getCmcBrand());
                goToDetailsOfCMC.putExtra("CMC_IMAGE",cmc.getCmcImage());
                goToDetailsOfCMC.putExtra("CMC_STATUS",cmc.getCmcStatus());
                goToDetailsOfCMC.putExtra("CMC_ADDRESS",cmc.getCmcLocation());
                context.startActivity(goToDetailsOfCMC);
                LogData.saveLog("SERVICE_CLICK",cmc.getCmcID(),"CMC","", "CMC_DETAILS");
            }
        });
    }

    @Override
    public int getItemCount() {
        return cmcs.size();
    }

    public class CMCViewHolder extends RecyclerView.ViewHolder{
        ImageView cmcImage;
        TextView cmcName, cmcLocation, cmcOwnerRate;
        LinearLayout masked;
        public CMCViewHolder(@NonNull View itemView) {
            super(itemView);
            cmcImage = itemView.findViewById(R.id.cmc_item_image);
            cmcName = itemView.findViewById(R.id.cmc_name_item);
            cmcLocation = itemView.findViewById(R.id.cmc_location_item);
            cmcOwnerRate = itemView.findViewById(R.id.cmc_rate_owner_item);
            masked = itemView.findViewById(R.id.most_asked_layout_cmc);
        }
    }
}
