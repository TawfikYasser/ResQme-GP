package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.common.RatingScreen;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Rate;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.example.resqme.serviceProvider.ServiceProviderAddService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
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
    FirebaseAuth firebaseAuth;

    public WinchRequestsAdapter(Context context, ArrayList<WinchRequest> winchRequests, DatabaseReference spDB) {
        this.context = context;
        this.winchRequests = winchRequests;
        this.serviceProviders = spDB;
        firebaseAuth = FirebaseAuth.getInstance();
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
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.rateBtn.setEnabled(false);
            holder.tvWinchRequestOwnerName.setText("غير متاح حتى قبول الطلب");
            holder.tvWinchRequestOwnerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvWinchRequestOwnerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvWinchRequestOwnerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Approved")){
            holder.tvWinchRequestStatus.setText("تم قبول الطلب.");
            holder.tvWinchRequestStatus.setTextColor(Color.GREEN);
            holder.TrackWinchBtn.setEnabled(true);
            holder.CompleteBtn.setEnabled(true);
            holder.CancelBtn.setEnabled(true);
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
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.rateBtn.setEnabled(true);
            holder.tvWinchRequestOwnerName.setText("غير متاح");
            holder.tvWinchRequestOwnerName.setTextColor(Color.RED);
            holder.tvWinchRequestOwnerPhone.setText("غير متاح");
            holder.tvWinchRequestOwnerPhone.setTextColor(Color.RED);
        }

        if(winchRequests.get(position).getWinchRequestStatus().equals("Success")){
            holder.tvWinchRequestStatus.setText("تم الطلب بنجاح");
            holder.tvWinchRequestStatus.setTextColor(Color.BLUE);
            holder.rateBtn.setEnabled(true);
            holder.TrackWinchBtn.setEnabled(false);
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);

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
        }

        if(winchRequests.get(position).getWinchRequestStatus().equals("Failed")){
            holder.tvWinchRequestStatus.setText("تم إلغاء الطلب");
            holder.tvWinchRequestStatus.setTextColor(Color.RED);
            holder.rateBtn.setEnabled(false);
            holder.TrackWinchBtn.setEnabled(false);
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);
            holder.tvWinchRequestOwnerName.setText("غير متاح");
            holder.tvWinchRequestOwnerName.setTextColor(Color.RED);
            holder.tvWinchRequestOwnerPhone.setText("غير متاح");
            holder.tvWinchRequestOwnerPhone.setTextColor(Color.RED);
        }

        holder.TrackWinchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToTracking = new Intent(context, TrackingWinchRequest.class);
                goToTracking.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goToTracking.putExtra("CUSTOMER_LAT",winchRequests.get(position).getCustomerLat());
                goToTracking.putExtra("CUSTOMER_LONG",winchRequests.get(position).getCustomerLong());
                goToTracking.putExtra("WINCH_ID",winchRequests.get(position).getWinchID());
                context.startActivity(goToTracking);
            }
        });

        holder.CompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Done
                DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                winches.child(winchRequests.get(position).getWinchID()).child("winchAvailability").setValue("Available");
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Success");
                Toast.makeText(context, "لقد قمت بإنهاء الطلب بنجاح، يمكنك تقييم الخدمة الآن.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Failed
                DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                winches.child(winchRequests.get(position).getWinchID()).child("winchAvailability").setValue("Available");
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Failed");
                Toast.makeText(context, "لقد قمت بإنهاء الطلب بشكل مفاجئ.", Toast.LENGTH_SHORT).show();
                holder.rateBtn.setEnabled(false);
                holder.TrackWinchBtn.setEnabled(false);
                holder.CompleteBtn.setEnabled(false);
                holder.CancelBtn.setEnabled(false);
            }
        });
        holder.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Rate in case of Done or Failed
                Intent mainIntent = new Intent(context, RatingScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.putExtra("FROM_REQUEST","CUSTOMER");
                mainIntent.putExtra("WINCH_REQUEST_ID", winchRequests.get(position).getWinchRequestID());
                mainIntent.putExtra("WINCH_REQUEST_SP_ID", winchRequests.get(position).getWinchOwnerID());
                mainIntent.putExtra("WINCH_REQUEST_CUSTOMER_ID", winchRequests.get(position).getCustomerID());
                context.startActivity(mainIntent);
            }
        });

        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
        rateTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Rate rate = dataSnapshot.getValue(Rate.class);
                    if(rate.getRequestID().equals(winchRequests.get(position).getWinchRequestID())
                    && rate.getCustomerID().equals(firebaseAuth.getCurrentUser().getUid())){
                        holder.rateBtn.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class MyWinchAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvWinchRequestStatus, tvWinchRequestName, tvWinchRequestOwnerName, tvWinchRequestOwnerPhone,
        tvWinchRequestCost, tvWinchRequestDescription, tvWinchRequestTimestamp;
        MaterialButton TrackWinchBtn, CompleteBtn, CancelBtn, rateBtn;
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
            CompleteBtn = itemView.findViewById(R.id.winch_request_complete_btn);
            CancelBtn = itemView.findViewById(R.id.winch_request_cancel_btn);
            rateBtn = itemView.findViewById(R.id.winch_request_rate_btn);
        }
    }
}
