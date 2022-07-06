package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.common.MyReports;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ServiceProviderHome_Winch extends AppCompatActivity {

    // Winch Data
    ImageView winchLicenceImageSPHome;
    TextView winchNameSPHome, winchStatusSPHome, winchAvailabilitySPHome, winchCostPerKMSPHome;
    MaterialButton changeWinchAvailabilityBTN;
    String winchAvailability = "", winchID = "";
    ShimmerFrameLayout shimmerFrameLayoutWinchSP;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home_winch);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = ServiceProviderHome_Winch.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        forceRTLIfSupported();
        shimmerFrameLayoutWinchSP = findViewById(R.id.winch_item_sp_shimmer_layout);
        shimmerFrameLayoutWinchSP.startShimmer();
        winchLicenceImageSPHome = findViewById(R.id.winch_licence_image_sp_home);
        winchNameSPHome = findViewById(R.id.winch_name_item_sp_home);
        winchCostPerKMSPHome = findViewById(R.id.winch_costperkm_sp_home);
        winchStatusSPHome = findViewById(R.id.winch_status_sp_home);
        winchAvailabilitySPHome = findViewById(R.id.winch_availability_sp_home);
        changeWinchAvailabilityBTN = findViewById(R.id.change_winch_availability_sp_home_btn);


        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_email = userData.getString("SP_EMAIL","SP_DEFAULT");
        String sp_username = userData.getString("SP_USERNAME","SP_DEFAULT");
        String sp_password = userData.getString("SP_PASSWORD","SP_DEFAULT");
        String sp_address = userData.getString("SP_ADDRESS","SP_DEFAULT");
        String sp_whatsapp = userData.getString("SP_WHATSAPP","SP_DEFAULT");
        String sp_dob = userData.getString("SP_DOB","SP_DEFAULT");
        String sp_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        String sp_usertype = userData.getString("SP_USERTYPE","SP_DEFAULT");
        String sp_usergender = userData.getString("SP_USERGENDER","SP_DEFAULT");
        String sp_userrate = userData.getString("SP_USERRATE","SP_DEFAULT");
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        String sp_serviceType = userData.getString("SP_ServiceType","SP_DEFAULT");
        String sp_cmc = userData.getString("SP_CMC","SP_DEFAULT");
        String sp_spareParts = userData.getString("SP_SPARE_PARTS","SP_DEFAULT");
        String sp_winch = userData.getString("SP_WINCH","SP_DEFAULT");


        // Will be moved to the activity
        DatabaseReference winchesTable = FirebaseDatabase.getInstance().getReference().child("Winches");
        winchesTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerFrameLayoutWinchSP.isShimmerStarted()){
                    shimmerFrameLayoutWinchSP.startShimmer();
                    shimmerFrameLayoutWinchSP.setVisibility(View.VISIBLE);
                }
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Winch winch = dataSnapshot.getValue(Winch.class);
                    if(winch.getWinchOwnerID().equals(sp_userid)){
                        // This is the right winch
                        // Fill the data
                        winchID = winch.getWinchID();
                        Glide.with(getApplicationContext()).load(winch.getWinchLicence()).into(winchLicenceImageSPHome);
                        winchNameSPHome.setText(winch.getWinchName());
                        winchCostPerKMSPHome.setText(winch.getWinchCostPerKM() + " جنيه لكل كيلو متر.");
                        if(winch.getWinchAvailability().equals("Available")){
                            winchAvailabilitySPHome.setText("متاح");
                            winchAvailabilitySPHome.setTextColor(Color.GREEN);
                            changeWinchAvailabilityBTN.setText("اجعل الونش غير متاح");
                            winchAvailability = "Available";
                            changeWinchAvailabilityBTN.setEnabled(true);
                        }else{
                            winchAvailabilitySPHome.setText("غير متاح");
                            winchAvailabilitySPHome.setTextColor(Color.RED);
                            changeWinchAvailabilityBTN.setText("اجعل الونش متاح");
                            winchAvailability = "Not Available";
                        }
                        if(winch.getWinchStatus().equals("Pending")){
                            winchAvailabilitySPHome.setText("غير متاح");
                            winchAvailabilitySPHome.setTextColor(Color.RED);
                            winchStatusSPHome.setText("يتم مراجعة بيانات الونش");
                            winchStatusSPHome.setTextColor(Color.rgb(255, 166, 53));
                        }else if(winch.getWinchStatus().equals("Approved")
                        && winch.getWinchAvailability().equals("Available")){
                            changeWinchAvailabilityBTN.setEnabled(true);
                            winchStatusSPHome.setText("تم قبول الونش");
                            winchStatusSPHome.setTextColor(Color.GREEN);
                        }else if(winch.getWinchStatus().equals("Refused")){
                            winchAvailabilitySPHome.setText("غير متاح");
                            winchAvailabilitySPHome.setTextColor(Color.RED);
                            winchStatusSPHome.setText("تم رفض الونش");
                            winchStatusSPHome.setTextColor(Color.RED);
                        }
                        if(winch.getWinchStatus().equals("Approved")){
                            winchStatusSPHome.setText("تم قبول الونش");
                            winchStatusSPHome.setTextColor(Color.GREEN);
                        }
                    }
                }
                shimmerFrameLayoutWinchSP.stopShimmer();
                shimmerFrameLayoutWinchSP.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        DatabaseReference winchRequests = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
        winchRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                    if(winchRequest.getWinchID().equals(winchID)
                    && winchRequest.getWinchRequestStatus().equals("Approved")){
                        // If the winch request is related to my winch and the status = Approved
                        // I can not change the winch availability
                        changeWinchAvailabilityBTN.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // Changing winch availability
        changeWinchAvailabilityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(winchAvailability.equals("Available")){
                    // Make it not available
                    DatabaseReference winchTable = FirebaseDatabase.getInstance().getReference().child("Winches");
                    winchTable.child(winchID).child("winchAvailability").setValue("Not Available");
                    LogData.saveLog("APP_CLICK","","","CLICK ON MAKE WINCH NOT AVAILABLE", "SERVICE_PROVIDER_HOME");
                }else{
                    // Make it available
                    DatabaseReference winchTable = FirebaseDatabase.getInstance().getReference().child("Winches");
                    winchTable.child(winchID).child("winchAvailability").setValue("Available");
                    LogData.saveLog("APP_CLICK","","","CLICK ON MAKE WINCH AVAILABLE", "SERVICE_PROVIDER_HOME");
                }
            }
        });





    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_SPHOMEWINCH);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("بيانات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServiceProviderHome_Winch.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}