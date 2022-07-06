package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.customer.AddCarData;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.customer.CustomerProfile;
import com.example.resqme.customer.CustomerUpdateProfile;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderProfile extends AppCompatActivity implements View.OnClickListener {
    CircleImageView spImage;
    TextView usernameTV, emailTV, addressTV, DOBTV, whatsAppTV, genderTV, spTypeTV, rateTV;
    Button updateProfileBtn;
    DatabaseReference serviceProviderTable;
    FirebaseAuth firebaseAuth;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = ServiceProviderProfile.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        serviceProviderTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        serviceProviderTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                    if (serviceProvider.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                        rateTV.setText(serviceProvider.getRate());
                        SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = cld.edit();
                        editor.putString("SP_USERRATE", String.valueOf(serviceProvider.getRate()));
                        editor.apply();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initViews();
        initToolbar();
        forceRTLIfSupported();
        showServiceProviderData();
    }
    void showServiceProviderData(){
        // Service Provider Information
        SharedPreferences userData = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_email = userData.getString("SP_EMAIL", "SP_DEFAULT");
        String sp_username = userData.getString("SP_USERNAME", "SP_DEFAULT");
        String sp_password = userData.getString("SP_PASSWORD", "SP_DEFAULT");
        String sp_address = userData.getString("SP_ADDRESS", "SP_DEFAULT");
        String sp_whatsapp = userData.getString("SP_WHATSAPP", "SP_DEFAULT");
        String sp_dob = userData.getString("SP_DOB", "SP_DEFAULT");
        String sp_userimage = userData.getString("SP_USERIMAGE", "SP_DEFAULT");
        String sp_usertype = userData.getString("SP_USERTYPE", "SP_DEFAULT");
        String sp_usergender = userData.getString("SP_USERGENDER", "SP_DEFAULT");
        String sp_userrate = userData.getString("SP_USERRATE", "SP_DEFAULT");
        String sp_userid = userData.getString("SP_USERID", "SP_DEFAULT");
        Glide.with(this).load(sp_userimage).into(spImage);
        usernameTV.setText(sp_username);
        emailTV.setText(sp_email);
        addressTV.setText(sp_address);
        DOBTV.setText(sp_dob);
        whatsAppTV.setText(sp_whatsapp);
        genderTV.setText(sp_usergender);
        spTypeTV.setText(sp_usertype);
        rateTV.setText(sp_userrate);

    }
    private void initViews() {

        spImage = findViewById(R.id.sp_profile_image);
        usernameTV = findViewById(R.id.sp_profile_text);
        emailTV = findViewById(R.id.sp_email_profile_text);
        addressTV = findViewById(R.id.sp_address_profile_text);
        DOBTV = findViewById(R.id.sp_dob_profile_text);
        whatsAppTV = findViewById(R.id.sp_whatsapp_profile_text);
        genderTV = findViewById(R.id.sp_gender_profile_text);
        spTypeTV = findViewById(R.id.spType_profile_text);
        rateTV = findViewById(R.id.spRate_profile_text);
        updateProfileBtn = findViewById(R.id.update_sp_profile_button);
        updateProfileBtn.setOnClickListener(this);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_sp_profile_button:
                Intent i = new Intent(ServiceProviderProfile.this, ServicProviderUpdateProfile.class);
                startActivity(i);
                LogData.saveLog("APP_CLICK","","","CLICK ON UPDATE PROFILE BUTTON", "SERVICE_PROVIDER_PROFILE");
                break;
        }
    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sp_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الصفحة الشخصية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServiceProviderProfile.this, R.style.Theme_ResQme);
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



    @Override
    protected void onResume() {
        super.onResume();
        showServiceProviderData();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

