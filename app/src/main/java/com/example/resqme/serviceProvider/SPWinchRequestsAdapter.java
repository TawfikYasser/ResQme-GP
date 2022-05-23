package com.example.resqme.serviceProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
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

public class SPWinchRequestsAdapter extends RecyclerView.Adapter<SPWinchRequestsAdapter.SPWinchRequestsAdapterViewHolder> {
    ArrayList<WinchRequest> winchRequests;
    Context context, context_2;
    View view;
    DatabaseReference customerDB;
    FirebaseAuth firebaseAuth;

    public SPWinchRequestsAdapter(ArrayList<WinchRequest> winchRequests, Context context, DatabaseReference customerDB, Context context_2, View view) {
        this.winchRequests = winchRequests;
        this.context = context;
        this.customerDB = customerDB;
        firebaseAuth = FirebaseAuth.getInstance();
        this.context_2 = context_2;
        this.view = view;
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
            holder.btnRateCustomer.setEnabled(false);
            holder.btnAcceptWinchRequestSP.setEnabled(true);
            holder.btnRefuseWinchRequestSP.setEnabled(true);
        }else if(winchRequests.get(position).getWinchRequestStatus().equals("Approved")){
            holder.tvSPWinchRequestStatus.setText("تم قبول الطلب.");
            holder.tvSPWinchRequestStatus.setTextColor(Color.GREEN);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(false);
            holder.tvSPWinchRequestCustomerName.setTextColor(Color.BLACK);
            holder.tvSPWinchRequestCustomerPhone.setTextColor(Color.BLACK);
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
            holder.btnRateCustomer.setEnabled(false);
        }

        if(winchRequests.get(position).getWinchRequestStatus().equals("Success")){
            holder.tvSPWinchRequestStatus.setText("تم الطلب بنجاح");
            holder.tvSPWinchRequestStatus.setTextColor(Color.BLUE);
            holder.btnAcceptWinchRequestSP.setEnabled(false);
            holder.btnRefuseWinchRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(true);
            holder.tvSPWinchRequestCustomerName.setTextColor(Color.BLACK);
            holder.tvSPWinchRequestCustomerPhone.setTextColor(Color.BLACK);
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
            holder.btnRateCustomer.setEnabled(true);
        }

        holder.btnAcceptWinchRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context_2)
                        .setTitle("هل أنت متأكد من قبول الطلب؟")
                        .setMessage("قبولك للطلب يعني انك ستقوم بالتواصل مع العميل لإتمام الخدمة")
                        .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Approved");
                                Toast.makeText(context, "لقد قمت بقبول الطلب.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("رجوع", null)
                        .show();

            }
        });

        holder.btnRefuseWinchRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context_2)
                        .setTitle("هل أنت متأكد من رفض الطلب؟")
                        .setMessage("رفض الطلب سيؤدي الى إنقاص تقييمك العام وسيتمكن العميل من تقييمك (تقييم سلبي على الارجح).")
                        .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                                winches.child(winchRequests.get(position).getWinchID()).child("winchAvailability").setValue("Available");
                                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                requestRef.child(winchRequests.get(position).getWinchRequestID()).child("winchRequestStatus").setValue("Refused");
                                Toast.makeText(context, "لقد قمت برفض الطلب.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("رجوع", null)
                        .show();
            }
        });

        holder.btnRateCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRateDialog(winchRequests.get(position).getWinchRequestID(),
                        winchRequests.get(position).getWinchOwnerID(),
                        winchRequests.get(position).getCustomerID());
            }
        });

        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
        rateTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Rate rate = dataSnapshot.getValue(Rate.class);
                    if(rate.getRequestID().equals(winchRequests.get(position).getWinchRequestID())){
                        if(winchRequests.get(position).getWinchOwnerID().equals(firebaseAuth.getCurrentUser().getUid())){
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

    private void openRateDialog(String winchRequestID, String winchOwnerID, String customerID) {

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
                                    Rate rate = new Rate(rateID, customerID, firebaseAuth.getCurrentUser().getUid(), String.valueOf(ratingBar.getRating()), rateText.getText().toString().trim(), winchRequestID, "ServiceProvider");
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
