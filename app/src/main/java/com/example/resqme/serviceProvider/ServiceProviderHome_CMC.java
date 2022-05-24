package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.CMC;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceProviderHome_CMC extends AppCompatActivity {
    // CMC Data
    ImageView cmcImageSPHome;
    TextView cmcNameSPHome, cmcLocationSPHome, cmcStatusSPHome, cmcAvailabilitySPHome, cmcBrandSPHome;
    MaterialButton changeCMCAvailability, cmcAddWinch;
    String cmcAvailability = "", cmcID = "";
    MaterialCardView winchCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home_cmc);
        initToolbar();
        forceRTLIfSupported();

        cmcImageSPHome = findViewById(R.id.cmc_item_image_sp_hme);
        cmcNameSPHome = findViewById(R.id.cmc_name_item_sp_home);
        cmcLocationSPHome = findViewById(R.id.cmc_location_item_sp_home);
        cmcStatusSPHome = findViewById(R.id.cmc_item_status_sp_home);
        cmcAvailabilitySPHome = findViewById(R.id.cmc_item_availability_sp_home);
        cmcBrandSPHome = findViewById(R.id.cmc_brand_item_sp_home);
        changeCMCAvailability = findViewById(R.id.change_cmc_availability_btn_sp_home);
        cmcAddWinch = findViewById(R.id.add_winch_cmc_case_only_BTN);


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
        DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
        cmcTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMC cmc = dataSnapshot.getValue(CMC.class);
                    if(cmc.getCmcServiceProviderId().equals(sp_userid)){
                        cmcID = cmc.getCmcID();
                        Glide.with(getApplicationContext()).load(cmc.getCmcImage()).into(cmcImageSPHome);
                        cmcNameSPHome.setText(cmc.getCmcName());
                        cmcLocationSPHome.setText(cmc.getCmcLocation());
                        cmcBrandSPHome.setText(cmc.getCmcBrand());
                        if(cmc.getCmcAvailability().equals("Available")){
                            cmcAvailabilitySPHome.setText("متاح");
                            cmcAvailabilitySPHome.setTextColor(Color.GREEN);
                            changeCMCAvailability.setText("اجعل مركز الخدمة غير متاح");
                            cmcAvailability = "Available";
                        }else{
                            cmcAvailabilitySPHome.setText("غير متاح");
                            cmcAvailabilitySPHome.setTextColor(Color.RED);
                            changeCMCAvailability.setText("اجعل مركز الخدمة متاح");
                            cmcAvailability = "Not Available";
                        }

                        if(cmc.getCmcStatus().equals("Pending")){
                            changeCMCAvailability.setEnabled(false);
                            cmcAvailabilitySPHome.setText("غير متاح");
                            cmcAvailabilitySPHome.setTextColor(Color.RED);

                            cmcStatusSPHome.setText("يتم مراجعة بيانات مركز الخدمة");
                            cmcStatusSPHome.setTextColor(Color.rgb(255, 166, 53));
                        }else if(cmc.getCmcStatus().equals("Approved")){
                            changeCMCAvailability.setEnabled(true);
                            cmcStatusSPHome.setText("تم قبول مركز الخدمة");
                            cmcStatusSPHome.setTextColor(Color.GREEN);
                        }else if(cmc.getCmcStatus().equals("Refused")){
                            cmcAvailabilitySPHome.setText("غير متاح");
                            cmcAvailabilitySPHome.setTextColor(Color.RED);

                            changeCMCAvailability.setEnabled(false);
                            cmcStatusSPHome.setText("تم رفض مركز الخدمة");
                            cmcStatusSPHome.setTextColor(Color.RED);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Will be moved to the activity
        // Changing cmc availability
        changeCMCAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cmcAvailability.equals("Available")){
                    // Make it not available
                    DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
                    cmcTable.child(cmcID).child("cmcAvailability").setValue("Not Available");
                    LogData.saveLog("APP_CLICK","","","CLICK ON MAKE CMC NOT AVAILABLE", "SERVICE_PROVIDER_HOME");
                }else{
                    // Make it available
                    DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
                    cmcTable.child(cmcID).child("cmcAvailability").setValue("Available");
                    LogData.saveLog("APP_CLICK","","","CLICK ON MAKE CMC AVAILABLE", "SERVICE_PROVIDER_HOME");
                }
            }
        });

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_SPHOMECMC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("بيانات مركز الصيانة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServiceProviderHome_CMC.this, R.style.Theme_ResQme);
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