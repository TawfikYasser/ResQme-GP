package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartsRequest;
import com.example.resqme.model.Winch;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SparePartsRequestAdapter extends RecyclerView.Adapter<SparePartsRequestAdapter.SparePartsRequestsAdapterViewHolder> {

    Context context;
    ArrayList<SparePartsRequest> sparePartsRequests;
    DatabaseReference referenceSP, sparePartsItemDB;

    public SparePartsRequestAdapter(Context context, ArrayList<SparePartsRequest> sparePartsRequests, DatabaseReference referenceSP, DatabaseReference sparePartsItemDB) {
        this.context = context;
        this.sparePartsRequests = sparePartsRequests;
        this.referenceSP = referenceSP;
        this.sparePartsItemDB = sparePartsItemDB;
    }

    @NonNull
    @Override
    public SparePartsRequestsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_parts_requests_item, parent, false);
        return new SparePartsRequestAdapter.SparePartsRequestsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SparePartsRequestsAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Whatever status
        holder.tvSpareRequestTimestamp.setText(sparePartsRequests.get(position).getSparePartsRequestTimestamp());
        holder.tvSpareRequestCost.setText(sparePartsRequests.get(position).getServiceCost());

        sparePartsItemDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                    if(sparePart.getItemID().equals(sparePartsRequests.get(position).getSparePartItemID())){
                        holder.tvSpareRequestName.setText(sparePart.getItemName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Status depending
        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Pending")){
            holder.tvSpareRequestStatus.setText("قيد المراجعة");
            holder.tvSpareRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.tvSpareRequestOwnerName.setText("غير متاح حتى قبول الطلب");
            holder.tvSpareRequestOwnerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSpareRequestOwnerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvSpareRequestOwnerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Approved")){
            holder.tvSpareRequestStatus.setText("تم قبول الطلب.");
            holder.tvSpareRequestStatus.setTextColor(Color.GREEN);
            holder.CompleteBtn.setEnabled(true);
            holder.CancelBtn.setEnabled(true);
            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            referenceSP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                        if(serviceProvider.getUserId().equals(sparePartsRequests.get(position).getSparePartOwnerID())){
                            holder.tvSpareRequestOwnerName.setText(serviceProvider.getUsername());
                            holder.tvSpareRequestOwnerPhone.setText(serviceProvider.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Refused")){
            holder.tvSpareRequestStatus.setText("تم رفض الطلب");
            holder.tvSpareRequestStatus.setTextColor(Color.RED);
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.tvSpareRequestOwnerName.setText("غير متاح");
            holder.tvSpareRequestOwnerName.setTextColor(Color.RED);
            holder.tvSpareRequestOwnerPhone.setText("غير متاح");
            holder.tvSpareRequestOwnerPhone.setTextColor(Color.RED);
        }


        holder.CompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "تم بنجاح", Toast.LENGTH_SHORT).show();
            }
        });

        holder.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "لم يتم إكمال الطلب", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return sparePartsRequests.size();
    }

    public class SparePartsRequestsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpareRequestStatus, tvSpareRequestName, tvSpareRequestOwnerName, tvSpareRequestOwnerPhone,
                tvSpareRequestCost, tvSpareRequestTimestamp;
        MaterialButton CompleteBtn, CancelBtn;
        public SparePartsRequestsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSpareRequestStatus = itemView.findViewById(R.id.spareParts_request_item_status_txt);
            tvSpareRequestName = itemView.findViewById(R.id.spareParts_request_item_spareParts_name_txt);
            tvSpareRequestOwnerName = itemView.findViewById(R.id.spareParts_request_item_spareParts_owner_name_txt);
            tvSpareRequestOwnerPhone = itemView.findViewById(R.id.spareParts_request_item_spareParts_owner_phone_txt);
            tvSpareRequestCost = itemView.findViewById(R.id.cmc_request_service_cost_txt);
            tvSpareRequestTimestamp = itemView.findViewById(R.id.spareParts_request_timestamp_txt);
            CompleteBtn = itemView.findViewById(R.id.spareParts_request_complete_btn);
            CancelBtn = itemView.findViewById(R.id.spareParts_request_cancel_btn);
        }
    }
}
