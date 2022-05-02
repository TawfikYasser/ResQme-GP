package com.example.resqme.customer;

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
import com.example.resqme.model.NotificationResQme;
import com.example.resqme.model.Rate;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.SparePartsRequest;
import com.example.resqme.model.Winch;
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

public class SparePartsRequestAdapter extends RecyclerView.Adapter<SparePartsRequestAdapter.SparePartsRequestsAdapterViewHolder> {

    Context context, context_2;
    ArrayList<SparePartsRequest> sparePartsRequests;
    DatabaseReference referenceSP, sparePartsItemDB;
    View view;
    FirebaseAuth firebaseAuth;

    public SparePartsRequestAdapter(Context context, ArrayList<SparePartsRequest> sparePartsRequests, DatabaseReference referenceSP,
                                    DatabaseReference sparePartsItemDB, Context context_2, View view) {
        this.context = context;
        this.sparePartsRequests = sparePartsRequests;
        this.referenceSP = referenceSP;
        this.sparePartsItemDB = sparePartsItemDB;
        this.context_2 = context_2;
        this.view = view;
        firebaseAuth = FirebaseAuth.getInstance();
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

            //Sending notification
            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
            NotificationResQme notification = new NotificationResQme(sparePartsRequests.get(position).getSparePartsRequestID(), "إشعار بخصوص طلب قطع غيار", "تم قبول طلبك من صاحب قطعة الغيار، لمعرفة المزيد من فضلك اذهب لصفحة طلبات قطع الغيار.", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).setValue(notification);


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

            //Sending notification
            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
            NotificationResQme notification = new NotificationResQme(sparePartsRequests.get(position).getSparePartsRequestID(), "إشعار بخصوص طلب قطع غيار", "للأسف تم رفض طلبك من صاحب قطعة الغيار، يمكنك تقييم الخدمة الآن.", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).setValue(notification);

        }


        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Success")){
            holder.tvSpareRequestStatus.setText("تم الطلب بنجاح");
            holder.tvSpareRequestStatus.setTextColor(Color.BLUE);
            holder.rateBtn.setEnabled(true);
            holder.CompleteBtn.setEnabled(false);
            holder.CancelBtn.setEnabled(false);

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
        }

        if(sparePartsRequests.get(position).getSparePartsRequestStatus().equals("Failed")){
            holder.tvSpareRequestStatus.setText("تم إلغاء الطلب");
            holder.tvSpareRequestStatus.setTextColor(Color.RED);
            holder.rateBtn.setEnabled(false);
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
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
                requestRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).child("sparePartsRequestStatus").setValue("Success");
                Toast.makeText(context, "لقد قمت بإنهاء الطلب بنجاح، يمكنك تقييم الخدمة الآن.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Failed
                DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
                requestRef.child(sparePartsRequests.get(position).getSparePartsRequestID()).child("sparePartsRequestStatus").setValue("Failed");
                Toast.makeText(context, "لقد قمت بإنهاء الطلب بشكل مفاجئ.", Toast.LENGTH_SHORT).show();
                holder.rateBtn.setEnabled(false);
                holder.CompleteBtn.setEnabled(false);
                holder.CancelBtn.setEnabled(false);
            }
        });

        holder.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                        Query query = FirebaseDatabase.getInstance().getReference("ServiceProviders").
                                orderByChild("userId").equalTo(sparePartOwnerID);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                                    double totalNewRate = (Double.parseDouble(serviceProvider.getRate()) + Double.parseDouble(String.valueOf(ratingBar.getRating()))) / 2;
                                    DatabaseReference spTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
                                    spTable.child(sparePartOwnerID).child("rate").setValue(String.valueOf(totalNewRate));
                                    // Save the rate in the rate table
                                    DatabaseReference rateTable = FirebaseDatabase.getInstance().getReference().child("Rate");
                                    String rateID = rateTable.push().getKey();
                                    Rate rate = new Rate(rateID, customerID, sparePartOwnerID, String.valueOf(ratingBar.getRating()), rateText.getText().toString().trim(), sparePartsRequestID, "Customer");
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

    public class SparePartsRequestsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpareRequestStatus, tvSpareRequestName, tvSpareRequestOwnerName, tvSpareRequestOwnerPhone,
                tvSpareRequestCost, tvSpareRequestTimestamp;
        MaterialButton CompleteBtn, CancelBtn, rateBtn;
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
            rateBtn = itemView.findViewById(R.id.spareParts_request_rate_from_customer_btn);
        }
    }
}
