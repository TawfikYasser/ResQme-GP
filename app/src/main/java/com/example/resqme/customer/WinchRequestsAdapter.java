package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WinchRequestsAdapter extends RecyclerView.Adapter<WinchRequestsAdapter.MyWinchAdapterViewHolder> {

    Context context;
    ArrayList<WinchRequest> winchRequests;
    DatabaseReference serviceProviders;

    public WinchRequestsAdapter(Context context, ArrayList<WinchRequest> winchRequests, DatabaseReference spDB) {
        this.context = context;
        this.winchRequests = winchRequests;
        this.serviceProviders = spDB;
    }

    @NonNull
    @Override
    public MyWinchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.winch_requests_item, parent, false);
        return new WinchRequestsAdapter.MyWinchAdapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return winchRequests.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyWinchAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Winches");

        // Whatever status
        holder.tvWinchRequestTimestamp.setText(winchRequests.get(position).getWinchRequestInitiationDate());
        holder.tvWinchRequestDescription.setText(winchRequests.get(position).getWinchRequestDescription());
        holder.tvWinchRequestCost.setText(winchRequests.get(position).getServiceCost());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Winch winch = dataSnapshot.getValue(Winch.class);
                    if(winch.getWinchID().equals(winchRequests.get(position).getWinchID())){
                        holder.tvWinchRequestName.setText(winch.getWinchName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Status depending
        if(winchRequests.get(position).getWinchRequestStatus().equals("Pending")){
            holder.tvWinchRequestStatus.setText("قيد المراجعة");
            holder.tvWinchRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.TrackWinchBtn.setEnabled(false);
            holder.tvWinchRequestOwnerName.setText("غير متاح حتى قبول الطلب");
            holder.tvWinchRequestOwnerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvWinchRequestOwnerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvWinchRequestOwnerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Approved")){
            holder.tvWinchRequestStatus.setText("تم قبول الطلب.");
            holder.tvWinchRequestStatus.setTextColor(Color.GREEN);
            holder.TrackWinchBtn.setEnabled(true);

            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            serviceProviders.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                        if(serviceProvider.getUserId().equals(winchRequests.get(position).getWinchOwnerID())){
                            holder.tvWinchRequestOwnerName.setText(serviceProvider.getUsername());
                            holder.tvWinchRequestOwnerPhone.setText(serviceProvider.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Refused")){
            holder.tvWinchRequestStatus.setText("تم رفض الطلب");
            holder.tvWinchRequestStatus.setTextColor(Color.RED);
            holder.TrackWinchBtn.setEnabled(false);
            holder.tvWinchRequestOwnerName.setText("غير متاح");
            holder.tvWinchRequestOwnerName.setTextColor(Color.RED);
            holder.tvWinchRequestOwnerPhone.setText("غير متاح");
            holder.tvWinchRequestOwnerPhone.setTextColor(Color.RED);
        }

    }

    class MyWinchAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvWinchRequestStatus, tvWinchRequestName, tvWinchRequestOwnerName, tvWinchRequestOwnerPhone,
        tvWinchRequestCost, tvWinchRequestDescription, tvWinchRequestTimestamp;
        MaterialButton TrackWinchBtn;
        public MyWinchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWinchRequestStatus = itemView.findViewById(R.id.winch_request_item_status_txt);
            tvWinchRequestName = itemView.findViewById(R.id.winch_request_item_winch_name_txt);
            tvWinchRequestOwnerName = itemView.findViewById(R.id.winch_request_item_winch_owner_name_txt);
            tvWinchRequestOwnerPhone = itemView.findViewById(R.id.winch_request_item_winch_owner_phone_txt);
            tvWinchRequestCost = itemView.findViewById(R.id.winch_request_item_service_cost_txt);
            tvWinchRequestDescription = itemView.findViewById(R.id.winch_request_item_description_txt);
            tvWinchRequestTimestamp = itemView.findViewById(R.id.winch_request_item_timestamp_txt);
            TrackWinchBtn = itemView.findViewById(R.id.winch_request_tracking_btn);
        }
    }
}
