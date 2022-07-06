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
import com.example.resqme.model.CMC;
import com.example.resqme.model.CMCRequest;
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

public class SPCMCRequestsAdapter extends RecyclerView.Adapter<SPCMCRequestsAdapter.SPCMCRequestsViewHolder> {

    ArrayList<CMCRequest> cmcRequests;
    Context context, context_2;
    DatabaseReference customersDB;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    View view;

    public SPCMCRequestsAdapter(ArrayList<CMCRequest> cmcRequests, Context context, Context context_2, DatabaseReference customersDB, View view) {
        this.cmcRequests = cmcRequests;
        this.context = context;
        this.context_2 = context_2;
        this.customersDB = customersDB;
        reference = FirebaseDatabase.getInstance().getReference().child("CMCs");
        firebaseAuth = FirebaseAuth.getInstance();
        this.view = view;
    }

    @NonNull
    @Override
    public SPCMCRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cmc_sp_requests_item, parent, false);
        return new SPCMCRequestsAdapter.SPCMCRequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPCMCRequestsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Whatever status
        holder.tvCMCSpareTime.setText(cmcRequests.get(position).getRequestCMCTimestamp());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMC cmc = dataSnapshot.getValue(CMC.class);
                    if(cmc.getCmcID().equals(cmcRequests.get(position).getCmcID())){
                        holder.tvSPCMCName.setText(cmc.getCmcName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Status depending
        if(cmcRequests.get(position).getCmcRequestStatus().equals("Pending")){
            holder.tvSPCMCRequestStatus.setText("قيد المراجعة");
            holder.tvSPCMCRequestStatus.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPCMCCustomerName.setText("غير متاح حتى قبول الطلب");
            holder.tvSPCMCCustomerName.setTextColor(Color.rgb(255, 166, 53));
            holder.tvSPCMCCustomerPhone.setText("غير متاح حتى قبول الطلب");
            holder.tvSPCMCCustomerPhone.setTextColor(Color.rgb(255, 166, 53));
            holder.btnRateCustomer.setEnabled(false);
            holder.btnAcceptCMCRequestSP.setEnabled(true);
            holder.btnRefuseCMCRequestSP.setEnabled(true);
        }else if(cmcRequests.get(position).getCmcRequestStatus().equals("Approved")){
            holder.tvSPCMCRequestStatus.setText("تم قبول الطلب.");
            holder.tvSPCMCRequestStatus.setTextColor(Color.GREEN);
            holder.btnAcceptCMCRequestSP.setEnabled(false);
            holder.btnRefuseCMCRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(false);
            holder.tvSPCMCCustomerName.setTextColor(Color.BLACK);
            holder.tvSPCMCCustomerPhone.setTextColor(Color.BLACK);
            //Getting winch name, owner name, owner phone using firebase
            //hide owner name and phone until approval
            customersDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(cmcRequests.get(position).getCustomerID())){
                            holder.tvSPCMCCustomerName.setText(customer.getUsername());
                            holder.tvSPCMCCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if(cmcRequests.get(position).getCmcRequestStatus().equals("Refused")){
            holder.tvSPCMCRequestStatus.setText("تم رفض الطلب");
            holder.tvSPCMCRequestStatus.setTextColor(Color.RED);
            holder.btnRefuseCMCRequestSP.setEnabled(false);
            holder.btnAcceptCMCRequestSP.setEnabled(false);
            holder.tvSPCMCCustomerName.setText("غير متاح");
            holder.tvSPCMCCustomerName.setTextColor(Color.RED);
            holder.tvSPCMCCustomerPhone.setText("غير متاح");
            holder.tvSPCMCCustomerPhone.setTextColor(Color.RED);
            holder.btnRateCustomer.setEnabled(false);
        }

        if(cmcRequests.get(position).getCmcRequestStatus().equals("Success")){
            holder.tvSPCMCRequestStatus.setText("تم الطلب بنجاح");
            holder.tvSPCMCRequestStatus.setTextColor(Color.BLUE);
            holder.btnAcceptCMCRequestSP.setEnabled(false);
            holder.btnRefuseCMCRequestSP.setEnabled(false);
            holder.btnRateCustomer.setEnabled(true);
            holder.tvSPCMCCustomerName.setTextColor(Color.BLACK);
            holder.tvSPCMCCustomerPhone.setTextColor(Color.BLACK);
            customersDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if(customer.getUserId().equals(cmcRequests.get(position).getCustomerID())){
                            holder.tvSPCMCCustomerName.setText(customer.getUsername());
                            holder.tvSPCMCCustomerPhone.setText(customer.getWhatsApp());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(cmcRequests.get(position).getCmcRequestStatus().equals("Failed")){
            holder.tvSPCMCRequestStatus.setText("تم إلغاء الطلب");
            holder.tvSPCMCRequestStatus.setTextColor(Color.RED);
            holder.btnAcceptCMCRequestSP.setEnabled(false);
            holder.btnRefuseCMCRequestSP.setEnabled(false);
            holder.tvSPCMCCustomerName.setText("غير متاح");
            holder.tvSPCMCCustomerName.setTextColor(Color.RED);
            holder.tvSPCMCCustomerPhone.setText("غير متاح");
            holder.tvSPCMCCustomerPhone.setTextColor(Color.RED);
            holder.btnRateCustomer.setEnabled(true);
        }

        holder.btnAcceptCMCRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context_2, R.style.AlertDialogCustom)
                        .setTitle("هل أنت متأكد من قبول الطلب؟")
                        .setMessage("قبولك للطلب يعني انك ستقوم بالتواصل مع العميل لإتمام الخدمة")
                        .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
                                requestRef.child(cmcRequests.get(position).getCmcRequestID()).child("cmcRequestStatus").setValue("Approved");
                                Toast.makeText(context, "لقد قمت بقبول الطلب.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("رجوع", null)
                        .show();
            }
        });

        holder.btnRefuseCMCRequestSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context_2, R.style.AlertDialogCustom)
                        .setTitle("هل أنت متأكد من رفض الطلب؟")
                        .setMessage("رفض الطلب سيؤدي الى إنقاص تقييمك العام وسيتمكن العميل من تقييمك (تقييم سلبي على الارجح).")
                        .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
                                requestRef.child(cmcRequests.get(position).getCmcRequestID()).child("cmcRequestStatus").setValue("Refused");
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
                // Open rate dialog
                openRateDialog(cmcRequests.get(position).getCmcRequestID(),
                        cmcRequests.get(position).getCmcOwnerID(),
                        cmcRequests.get(position).getCustomerID());
            }
        });

        DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
        rateTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Rate rate = dataSnapshot.getValue(Rate.class);
                    if(rate.getRequestID().equals(cmcRequests.get(position).getCmcRequestID())
                    ){
                        if(cmcRequests.get(position).getCmcOwnerID().equals(firebaseAuth.getCurrentUser().getUid())){
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

    @Override
    public int getItemCount() {
        return cmcRequests.size();
    }

    private void openRateDialog(String cmcRequestID, String cmcOwnerID, String customerID) {

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
                        new AlertDialog.Builder(context_2, R.style.AlertDialogCustom)
                                .setTitle("تقييم العميل")
                                .setMessage("هل أنت متأكد من إرسال هذا التقييم؟")
                                .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
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
                                                    Rate rate = new Rate(rateID, customerID, firebaseAuth.getCurrentUser().getUid(), String.valueOf(ratingBar.getRating()), rateText.getText().toString().trim(), cmcRequestID, "ServiceProvider");
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
                                })
                                .setNegativeButton("رجوع", null)
                                .show();
                    }
                }else{
                    Toast.makeText(context, "من فضلك قم بكتابة تقييم...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public class SPCMCRequestsViewHolder extends RecyclerView.ViewHolder{
        TextView tvSPCMCRequestStatus, tvSPCMCName, tvSPCMCCustomerName, tvSPCMCCustomerPhone,
                 tvCMCSpareTime;
        MaterialButton btnAcceptCMCRequestSP, btnRefuseCMCRequestSP, btnRateCustomer;
        public SPCMCRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSPCMCRequestStatus = itemView.findViewById(R.id.sp_cmc_request_item_status_txt);
            tvSPCMCName = itemView.findViewById(R.id.sp_cmc_request_item_cmc_name_txt);
            tvSPCMCCustomerName = itemView.findViewById(R.id.sp_cmc_request_item_cmc_customer_name_txt);
            tvSPCMCCustomerPhone = itemView.findViewById(R.id.sp_cmc_request_item_cmc_customer_phone_txt);
            tvCMCSpareTime = itemView.findViewById(R.id.sp_cmc_request_timestamp_txt);
            btnAcceptCMCRequestSP = itemView.findViewById(R.id.sp_cmc_request_complete_btn);
            btnRefuseCMCRequestSP = itemView.findViewById(R.id.sp_cmc_request_cancel_btn);
            btnRateCustomer = itemView.findViewById(R.id.cmc_request_rate_from_sp_btn);
        }
    }
}
