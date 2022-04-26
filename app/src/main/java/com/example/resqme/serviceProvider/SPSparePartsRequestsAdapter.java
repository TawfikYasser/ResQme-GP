package com.example.resqme.serviceProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.Customer;
import com.example.resqme.model.Rate;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartsRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SPSparePartsRequestsAdapter extends RecyclerView.Adapter<SPSparePartsRequestsAdapter.SPSparePartsRequestsViewHolder> {

    ArrayList<SparePartsRequest> sparePartsRequests;
    Context context, context_2;
    DatabaseReference customersDB;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    View view;

    public SPSparePartsRequestsAdapter(ArrayList<SparePartsRequest> sparePartsRequests, Context context, DatabaseReference customersDB, Context context_2, View view) {
        this.sparePartsRequests = sparePartsRequests;
        this.context = context;
        this.context_2 = context_2;
        this.customersDB = customersDB;
        this.view = view;
        reference = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public SPSparePartsRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_parts_sp_requests, parent, false);
        return new SPSparePartsRequestsAdapter.SPSparePartsRequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPSparePartsRequestsViewHolder holder, @SuppressLint("RecyclerView") int position) {



        // Whatever status
        holder.tvSPSpareTime.setText(sparePartsRequests.get(position).getSparePartsRequestTimestamp());
        holder.tvSPSpareCost.setText(sparePartsRequests.get(position).getServiceCost());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                    if(sparePart.getItemID().equals(sparePartsRequests.get(position).getSparePartItemID())){
                        holder.tvSPSpareName.setText(sparePart.getItemName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Status depending
        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Pending")){
            holder.tvSPSpareRequestStatus.setText("قيد المراجعة");
            holder.tvSPSpareRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPSpareCustomerName.setText("غير متاح حتى قبول الطلب");
            holder.tvSPSpareCustomerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPSpareCustomerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvSPSpareCustomerPhone.setTextColor(Color.rgb(255, 166, 53));
        }else if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Approved")){
            holder.tvSPSpareRequestStatus.setText("تم قبول الطلب.");
            holder.tvSPSpareRequestStatus.setTextColor(Color.GREEN);
            holder.btnAcceptSpareRequestSP.setEnabled(false);
            holder.btnRefuseSpareRequestSP.setEnabled(false);
            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            customersDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(sparePartsRequests.get(position).getCustomerID())){
                            holder.tvSPSpareCustomerName.setText(customer.getUsername());
                            holder.tvSPSpareCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Refused")){
            holder.tvSPSpareRequestStatus.setText("تم رفض الطلب");
            holder.tvSPSpareRequestStatus.setTextColor(Color.RED);
            holder.btnRefuseSpareRequestSP.setEnabled(false);
            holder.btnAcceptSpareRequestSP.setEnabled(false);
            holder.tvSPSpareCustomerName.setText("غير متاح");
            holder.tvSPSpareCustomerName.setTextColor(Color.RED);
            holder.tvSPSpareCustomerPhone.setText("غير متاح");
            holder.tvSPSpareCustomerPhone.setTextColor(Color.RED);
        }

        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Success")){
            holder.tvSPSpareRequestStatus.setText("تم الطلب بنجاح");
            holder.tvSPSpareRequestStatus.setTextColor(Color.BLUE);
            holder.btnAcceptSpareRequestSP.setEnabled(false);
            holder.btnRefuseSpareRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(true);
            customersDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(sparePartsRequests.get(position).getCustomerID())){
                            holder.tvSPSpareCustomerName.setText(customer.getUsername());
                            holder.tvSPSpareCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Failed")){
            holder.tvSPSpareRequestStatus.setText("تم إلغاء الطلب");
            holder.tvSPSpareRequestStatus.setTextColor(Color.RED);
            holder.btnAcceptSpareRequestSP.setEnabled(false);
            holder.btnRefuseSpareRequestSP.setEnabled(false);
            holder.tvSPSpareCustomerName.setText("غير متاح");
            holder.tvSPSpareCustomerName.setTextColor(Color.RED);
            holder.tvSPSpareCustomerPhone.setText("غير متاح");
            holder.tvSPSpareCustomerPhone.setTextColor(Color.RED);
        }

        holder.btnAcceptSpareRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
                requestRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).child("sparePartsRequestStatus").setValue("Approved");
                Toast.makeText(context, "لقد قمت بقبول الطلب.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRefuseSpareRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("SpareParts");
                winches.child(sparePartsRequests.get(position).getSparePartItemID()).child("itemAvailability").setValue("Available");
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
                requestRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).child("sparePartsRequestStatus").setValue("Refused");
                Toast.makeText(context, "لقد قمت برفض الطلب.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRateCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open rate dialog
                openRateDialog(sparePartsRequests.get(position).getSparePartsRequestID(),
                        sparePartsRequests.get(position).getSparePartOwnerID(),
                        sparePartsRequests.get(position).getCustomerID());
            }
        });

        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
        rateTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Rate rate = dataSnapshot.getValue(Rate.class);
                    if(rate.getRequestID().equals(sparePartsRequests.get(position).getSparePartsRequestID())
                            ){
                        if(sparePartsRequests.get(position).getSparePartOwnerID().equals(firebaseAuth.getCurrentUser().getUid())){
                            if(rate.getRateFrom().equals("ServiceProvider")){
                                holder.btnRateCustomer.setEnabled(false);
                            }else{
                                holder.btnRateCustomer.setEnabled(true);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openRateDialog(String sparePartsRequestID, String sparePartOwnerID, String customerID) {

        BottomSheetDialog rateDialog;
        rateDialog = new BottomSheetDialog(context_2, R.style.BottomSheetDialogTheme);

        View rateBottomView = LayoutInflater.from(context_2).inflate(R.layout.rate_bottom_layout,
                (LinearLayout) view.findViewById(R.id.bottom_sheet_rate_linear_layout));

        RatingBar ratingBar = rateBottomView.findViewById(R.id.rating_bar_page);
        TextInputEditText rateText = rateBottomView.findViewById(R.id.rating_description_et);
        MaterialButton saveRateBtn = rateBottomView.findViewById(R.id.save_rating_btn);


        rateDialog.setContentView(rateBottomView);
        rateDialog.show();

        saveRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(rateText.getText().toString().trim())){
                    if(String.valueOf(ratingBar.getRating()).equals("0.0")){
                        Toast.makeText(context_2, "من فضلك اختر تقييم من 1 الى 5", Toast.LENGTH_SHORT).show();
                    }else{
                        // We are service provider
                        ProgressDialog progressDialog = new ProgressDialog(context_2);
                        progressDialog.setMessage("انتظر قليلاً...");
                        progressDialog.show();
                        Query query = FirebaseDatabase.getInstance().getReference("Customer").
                                orderByChild("userId").equalTo(customerID);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Customer customer = dataSnapshot.getValue(Customer.class);
                                    double totalNewRate = (Double.parseDouble(customer.getRate()) + Double.parseDouble(String.valueOf(ratingBar.getRating()))) / 2;
                                    DatabaseReference customerTable = FirebaseDatabase.getInstance().getReference().child("Customer");
                                    customerTable.child(customerID).child("rate").setValue(String.valueOf(totalNewRate));
                                    // Save the rate in the rate table
                                    DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
                                    String rateID = rateTable.push().getKey();
                                    Rate rate = new Rate(rateID, customerID, firebaseAuth.getCurrentUser().getUid(), String.valueOf(ratingBar.getRating()), rateText.getText().toString().trim(), sparePartsRequestID, "ServiceProvider");
                                    rateTable.child(rateID).setValue(rate);

                                    progressDialog.dismiss();
                                    Toast.makeText(context_2, "تمت عملية التقييم بنجاح!", Toast.LENGTH_SHORT).show();
                                    rateDialog.cancel();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }else{
                    Toast.makeText(context, "من فضلك قم بكتابة تقييم...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return sparePartsRequests.size();
    }

    public class SPSparePartsRequestsViewHolder extends RecyclerView.ViewHolder{
        TextView tvSPSpareRequestStatus, tvSPSpareName, tvSPSpareCustomerName, tvSPSpareCustomerPhone,
                tvSPSpareCost, tvSPSpareTime;
        MaterialButton btnAcceptSpareRequestSP, btnRefuseSpareRequestSP, btnRateCustomer;
        public SPSparePartsRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSPSpareRequestStatus = itemView.findViewById(R.id.sp_spareParts_request_item_status_txt);
            tvSPSpareName = itemView.findViewById(R.id.sp_spareParts_request_item_spareParts_name_txt);
            tvSPSpareCustomerName = itemView.findViewById(R.id.sp_spareParts_request_item_spareParts_customer_name_txt);
            tvSPSpareCustomerPhone = itemView.findViewById(R.id.sp_spareParts_request_item_spareParts_customer_phone_txt);
            tvSPSpareCost = itemView.findViewById(R.id.sp_spareParts_request_service_cost_txt);
            tvSPSpareTime = itemView.findViewById(R.id.sp_spareParts_request_timestamp_txt);

            btnAcceptSpareRequestSP = itemView.findViewById(R.id.sp_spareParts_request_complete_btn);
            btnRefuseSpareRequestSP = itemView.findViewById(R.id.sp_spareParts_request_cancel_btn);
            btnRateCustomer = itemView.findViewById(R.id.spareParts_request_rate_from_sp_btn);
        }
    }
}
