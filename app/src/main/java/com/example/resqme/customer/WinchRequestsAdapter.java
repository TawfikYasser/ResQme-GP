package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.NotificationResQme;
import com.example.resqme.model.Rate;
import com.example.resqme.model.RequestDetailsModel;
import com.example.resqme.model.ServiceProvider;
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

public class WinchRequestsAdapter extends RecyclerView.Adapter<WinchRequestsAdapter.MyWinchAdapterViewHolder> {

    Context context, context_2;
    ArrayList<WinchRequest> winchRequests;
    DatabaseReference serviceProviders;
    FirebaseAuth firebaseAuth;
    View view_2;
    String fixType = "";

    public WinchRequestsAdapter(Context context, ArrayList<WinchRequest> winchRequests, DatabaseReference spDB, View view, Context context_2) {
        this.context = context;
        this.winchRequests = winchRequests;
        this.serviceProviders = spDB;
        this.view_2 = view;
        firebaseAuth = FirebaseAuth.getInstance();
        this.context_2 = context_2;
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

            //Sending notification
            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
            NotificationResQme notification = new NotificationResQme(winchRequests.get(position).getWinchRequestID(), "إشعار بخصوص طلبك للونش", "تم قبول طلبك من صاحب الونش، يمكنك الآن تتبع حركة الونش في صفحة طلبات الونش.", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationRef.child(winchRequests.get(position).getWinchRequestID()).setValue(notification);

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

            //Sending notification
            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
            NotificationResQme notification = new NotificationResQme(winchRequests.get(position).getWinchRequestID(), "إشعار بخصوص طلبك للونش", "للأسف تم رفض طلبك من صاحب الونش، يمكنك تقييم الآن تقييم الخدمة.", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationRef.child(winchRequests.get(position).getWinchRequestID()).setValue(notification);



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
                // Getting the request details, then make the request success and allow rate
                openRequestDetailsBottomSheet(winchRequests.get(position).getWinchID(),
                        winchRequests.get(position).getWinchRequestID());
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

    private void openRateDialog(String winchRequestID, String winchOwnerID, String customerID) {
        BottomSheetDialog rateDialog;
        rateDialog = new BottomSheetDialog(context_2, R.style.BottomSheetDialogTheme);

        View rateBottomView = LayoutInflater.from(context_2).inflate(R.layout.rate_bottom_layout,
                (LinearLayout) view_2.findViewById(R.id.bottom_sheet_rate_linear_layout));

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

                        Query query = FirebaseDatabase.getInstance().getReference("ServiceProviders").
                                orderByChild("userId").equalTo(winchOwnerID);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                                    double totalNewRate = (Double.parseDouble(serviceProvider.getRate()) + Double.parseDouble(String.valueOf(ratingBar.getRating()))) / 2;
                                    DatabaseReference spTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
                                    spTable.child(winchOwnerID).child("rate").setValue(String.valueOf(totalNewRate));
                                    // Save the rate in the rate table
                                    DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
                                    String rateID = rateTable.push().getKey();
                                    Rate rate = new Rate(rateID, customerID, winchOwnerID, String.valueOf(ratingBar.getRating()), rateText.getText().toString().trim(), winchRequestID, "Customer");
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

    private void openRequestDetailsBottomSheet(String winchID, String winchRequestID) {
        BottomSheetDialog detailsDialog;
        detailsDialog = new BottomSheetDialog(context_2, R.style.BottomSheetDialogTheme);

        View requestBottomView = LayoutInflater.from(context_2).inflate(R.layout.request_details_bottom_layout,
                (LinearLayout) view_2.findViewById(R.id.bottom_sheet_request_details_linear_layout));

        TextInputEditText otherET = requestBottomView.findViewById(R.id.request_details_other_et_bs);
        Button sendBtn = requestBottomView.findViewById(R.id.send_request_details_btn_bs);
        AutoCompleteTextView requestsFixes = requestBottomView.findViewById(R.id.request_details_dropdown_list_bs);

        String[] arrayFixTypes = view_2.getResources().getStringArray(R.array.fixTypes);
        ArrayAdapter<String> adapterFixTypes = new ArrayAdapter<String>
                (context, R.layout.item_add_car_data_dropdownlist, arrayFixTypes);
        requestsFixes.setThreshold(1);
        requestsFixes.setAdapter(adapterFixTypes);

        requestsFixes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                fixType = item.toString().trim();
                if(fixType.equals("أخرى")){
                    otherET.setEnabled(true);
                } else {
                    otherET.setEnabled(false);
                }
            }
        });

        detailsDialog.setContentView(requestBottomView);
        detailsDialog.show();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fixType.equals("أخرى")){
                    if(!TextUtils.isEmpty(otherET.getText().toString().trim())){
                        // Not Empty
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "0", "0", "0", "0", "0",
                                otherET.getText().toString().trim()
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                        Toast.makeText(context, "تم حفظ تفاصيل الطلب.", Toast.LENGTH_SHORT).show();
                        // do the work
                        DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                        winches.child(winchID).child("winchAvailability").setValue("Available");
                        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                        requestRef.child(winchRequestID).child("winchRequestStatus").setValue("Success");
                        Toast.makeText(context, "لقد قمت بإنهاء الطلب بنجاح، يمكنك تقييم الخدمة الآن.", Toast.LENGTH_SHORT).show();
                        detailsDialog.cancel();
                    } else {
                        // Empty
                        Toast.makeText(context, "يجب إدخال وصف للإصلاحات في حالة إختيار (أخرى).", Toast.LENGTH_SHORT).show();
                    }
                }else if(fixType.equals("إضاءات العربية")
                || fixType.equals("البطارية")
                || fixType.equals("المحرك")
                || fixType.equals("الزيت")
                || fixType.equals("الفلاتر")
                || fixType.equals("العجلات")){
                    if(fixType.equals("إضاءات العربية")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "1", "0", "0", "0", "0", "0","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }else if(fixType.equals("البطارية")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "1", "0", "0", "0", "0","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }else if(fixType.equals("الزيت")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "0", "1", "0", "0", "0","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }else if(fixType.equals("الفلاتر")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "0", "0", "1", "0", "0","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }else if(fixType.equals("المحرك")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "0", "0", "0", "0", "1","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }else if(fixType.equals("العجلات")){
                        // save request details
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        DatabaseReference requestDetailsTable = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
                        String requestDetailsID = requestDetailsTable.push().getKey();
                        RequestDetailsModel requestDetails = new RequestDetailsModel(
                                requestDetailsID, firebaseAuth.getCurrentUser().getUid().toString(), winchRequestID,
                                "0", "0", "0", "0", "1", "0","0"
                        );
                        requestDetailsTable.child(requestDetailsID).setValue(requestDetails);
                    }
                    Toast.makeText(context, "تم حفظ تفاصيل الطلب.", Toast.LENGTH_SHORT).show();
                    // do the work
                    DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
                    winches.child(winchID).child("winchAvailability").setValue("Available");
                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                    requestRef.child(winchRequestID).child("winchRequestStatus").setValue("Success");
                    Toast.makeText(context, "لقد قمت بإنهاء الطلب بنجاح، يمكنك تقييم الخدمة الآن.", Toast.LENGTH_SHORT).show();
                    detailsDialog.cancel();
                }
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
