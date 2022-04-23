package com.example.resqme.serviceProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.RatingScreen;
import com.example.resqme.customer.TrackingWinchRequest;
import com.example.resqme.customer.WinchRequestsAdapter;
import com.example.resqme.model.Customer;
import com.example.resqme.model.Rate;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SPWinchRequestsAdapter extends RecyclerView.Adapter<SPWinchRequestsAdapter.SPWinchRequestsAdapterViewHolder> {
    ArrayList<WinchRequest> winchRequests;
    Context context;
    DatabaseReference customerDB;
    FirebaseAuth firebaseAuth;

    public SPWinchRequestsAdapter(ArrayList<WinchRequest> winchRequests, Context context, DatabaseReference customerDB) {
        this.winchRequests = winchRequests;
        this.context = context;
        this.customerDB = customerDB;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public SPWinchRequestsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.winch_requests_sp_item, parent, false);
        return new SPWinchRequestsAdapter.SPWinchRequestsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPWinchRequestsAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Winches");

        // Whatever status
        holder.tvSPWinchRequestTime.setText(winchRequests.get(position).getWinchRequestInitiationDate());
        holder.tvSPWinchRequestDescription.setText(winchRequests.get(position).getWinchRequestDescription());
        holder.tvSPWinchRequestCost.setText(winchRequests.get(position).getServiceCost());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Winch winch = dataSnapshot.getValue(Winch.class);
                    if(winch.getWinchID().equals(winchRequests.get(position).getWinchID())){
                        holder.tvSPWinchRequestWinchName.setText(winch.getWinchName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Status depending
        if(winchRequests.get(position).getWinchRequestStatus().equals("Pending")){
            holder.tvSPWinchRequestStatus.setText("قيد المراجعة");
            holder.tvSPWinchRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPWinchRequestCustomerName.setText("غير متاح حتى قبول الطلب");
            holder.tvSPWinchRequestCustomerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPWinchRequestCustomerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvSPWinchRequestCustomerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Approved")){
            holder.tvSPWinchRequestStatus.setText("تم قبول الطلب.");
            holder.tvSPWinchRequestStatus.setTextColor(Color.GREEN);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            customerDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(winchRequests.get(position).getCustomerID())){
                            holder.tvSPWinchRequestCustomerName.setText(customer.getUsername());
                            holder.tvSPWinchRequestCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Refused")){
            holder.tvSPWinchRequestStatus.setText("تم رفض الطلب");
            holder.tvSPWinchRequestStatus.setTextColor(Color.RED);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.tvSPWinchRequestCustomerName.setText("غير متاح");
            holder.tvSPWinchRequestCustomerName.setTextColor(Color.RED);
            holder.tvSPWinchRequestCustomerPhone.setText("غير متاح");
            holder.tvSPWinchRequestCustomerPhone.setTextColor(Color.RED);
        }

        if(winchRequests.get(position).getWinchRequestStatus().equals("Success")){
            holder.tvSPWinchRequestStatus.setText("تم الطلب بنجاح");
            holder.tvSPWinchRequestStatus.setTextColor(Color.BLUE);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(true);
            customerDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(winchRequests.get(position).getCustomerID())){
                            holder.tvSPWinchRequestCustomerName.setText(customer.getUsername());
                            holder.tvSPWinchRequestCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(winchRequests.get(position).getWinchRequestStatus().equals("Failed")){
            holder.tvSPWinchRequestStatus.setText("تم إلغاء الطلب");
            holder.tvSPWinchRequestStatus.setTextColor(Color.RED);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            holder.tvSPWinchRequestCustomerName.setText("غير متاح");
            holder.tvSPWinchRequestCustomerName.setTextColor(Color.RED);
            holder.tvSPWinchRequestCustomerPhone.setText("غير متاح");
            holder.tvSPWinchRequestCustomerPhone.setTextColor(Color.RED);
        }

        holder.btnAcceptWinchRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Approved");
                Toast.makeText(context, "لقد قمت بقبول الطلب.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRefuseWinchRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                winches.child(winchRequests.get(position).getWinchID()).child("winchAvailability").setValue("Available");
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Refused");
                Toast.makeText(context, "لقد قمت برفض الطلب.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRateCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Rate in case of Done or Failed
                Intent mainIntent = new Intent(context, RatingScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.putExtra("FROM_REQUEST","SP");
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
                            && rate.getSpId().equals(firebaseAuth.getCurrentUser().getUid())){
                        holder.btnRateCustomer.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return winchRequests.size();
    }

    public class SPWinchRequestsAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvSPWinchRequestStatus, tvSPWinchRequestWinchName, tvSPWinchRequestCustomerName, tvSPWinchRequestCustomerPhone,
                tvSPWinchRequestCost, tvSPWinchRequestDescription, tvSPWinchRequestTime;
        MaterialButton btnAcceptWinchRequestSP, btnRefuseWinchRequestSP, btnRateCustomer;
        public SPWinchRequestsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAcceptWinchRequestSP = itemView.findViewById(R.id.sp_winch_request_item_accept_btn);
            btnRefuseWinchRequestSP = itemView.findViewById(R.id.sp_winch_request_item_refuse_btn);

            tvSPWinchRequestStatus = itemView.findViewById(R.id.sp_winch_request_item_status_txt);
            tvSPWinchRequestWinchName = itemView.findViewById(R.id.sp_winch_request_item_winch_name_txt);
            tvSPWinchRequestCustomerName = itemView.findViewById(R.id.winch_request_item_customer_name_txt);
            tvSPWinchRequestCustomerPhone = itemView.findViewById(R.id.winch_request_item_customer_phone_txt);
            tvSPWinchRequestCost = itemView.findViewById(R.id.sp_winch_request_item_service_cost_txt);
            tvSPWinchRequestDescription = itemView.findViewById(R.id.sp_winch_request_item_description_txt);
            tvSPWinchRequestTime = itemView.findViewById(R.id.sp_winch_request_item_timestamp_txt);
            btnRateCustomer = itemView.findViewById(R.id.sp_winch_request_item_rate_btn);
        }
    }
}
