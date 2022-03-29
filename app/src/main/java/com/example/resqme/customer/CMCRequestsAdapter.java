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
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.ServiceProvider;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CMCRequestsAdapter extends RecyclerView.Adapter<CMCRequestsAdapter.CMCRequestsAdapterViewHolder> {
    Context context;
    ArrayList<CMCRequest> cmcRequests;
    DatabaseReference referenceSP;

    public CMCRequestsAdapter(Context context, ArrayList<CMCRequest> cmcRequests, DatabaseReference referenceSP) {
        this.context = context;
        this.cmcRequests = cmcRequests;
        this.referenceSP = referenceSP;
    }

    @NonNull
    @Override
    public CMCRequestsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cmc_requests_item, parent, false);
        return new CMCRequestsAdapter.CMCRequestsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CMCRequestsAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // Whatever status
        holder.tvCMCRequestTimestamp.setText(cmcRequests.get(position).getRequestCMCTimestamp());
        holder.tvCMCRequestDescription.setText(cmcRequests.get(position).getCmcRequestDescription());

        // Status depending
        if(cmcRequests.get(position).getCmcRequestStatus().equals("Pending")){
            holder.tvCMCRequestStatus.setText("قيد المراجعة");
            holder.tvCMCRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.tvCMCRequestOwnerName.setText("غير متاح حتى قبول الطلب");
            holder.tvCMCRequestOwnerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvCMCRequestOwnerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvCMCRequestOwnerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(cmcRequests.get(position).getCmcRequestStatus().equals("Approved")){
            holder.tvCMCRequestStatus.setText("تم قبول الطلب.");
            holder.tvCMCRequestStatus.setTextColor(Color.GREEN);
            holder.CompleteBtn.setEnabled(true);
            holder.CancelBtn.setEnabled(true);
            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            referenceSP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                        if(serviceProvider.getUserId().equals(cmcRequests.get(position).getCmcOwnerID())){
                            holder.tvCMCRequestOwnerName.setText(serviceProvider.getUsername());
                            holder.tvCMCRequestOwnerPhone.setText(serviceProvider.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(cmcRequests.get(position).getCmcRequestStatus().equals("Refused")){
            holder.tvCMCRequestStatus.setText("تم رفض الطلب");
            holder.tvCMCRequestStatus.setTextColor(Color.RED);
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.tvCMCRequestOwnerName.setText("غير متاح");
            holder.tvCMCRequestOwnerName.setTextColor(Color.RED);
            holder.tvCMCRequestOwnerPhone.setText("غير متاح");
            holder.tvCMCRequestOwnerPhone.setTextColor(Color.RED);
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
        return cmcRequests.size();
    }

    public class CMCRequestsAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvCMCRequestStatus, tvCMCRequestName, tvCMCRequestOwnerName, tvCMCRequestOwnerPhone,
                tvWinchRequestCost, tvCMCRequestDescription, tvCMCRequestTimestamp;
        MaterialButton CompleteBtn, CancelBtn;
        public CMCRequestsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCMCRequestStatus = itemView.findViewById(R.id.cmc_request_item_status_txt);
            tvCMCRequestName = itemView.findViewById(R.id.cmc_request_item_cmc_name_txt);
            tvCMCRequestOwnerName = itemView.findViewById(R.id.cmc_request_item_cmc_owner_name_txt);
            tvCMCRequestOwnerPhone = itemView.findViewById(R.id.cmc_request_item_cmc_owner_phone_txt);
            tvCMCRequestDescription = itemView.findViewById(R.id.cmc_request_description_txt);
            tvCMCRequestTimestamp = itemView.findViewById(R.id.cmc_request_timestamp_txt);
            CompleteBtn = itemView.findViewById(R.id.cmc_request_complete_btn);
            CancelBtn = itemView.findViewById(R.id.cmc_request_cancel_btn);
        }
    }
}
