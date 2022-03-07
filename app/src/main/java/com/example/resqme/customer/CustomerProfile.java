package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.resqme.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfile extends AppCompatActivity {
    CircleImageView customerImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        initViews();
        initToolbar();
        forceRTLIfSupported();

        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_email = userData.getString("C_EMAIL","C_DEFAULT");
        String c_username = userData.getString("C_USERNAME","C_DEFAULT");
        String c_password = userData.getString("C_PASSWORD","C_DEFAULT");
        String c_address = userData.getString("C_ADDRESS","C_DEFAULT");
        String c_whatsapp = userData.getString("C_WHATSAPP","C_DEFAULT");
        String c_dob = userData.getString("C_DOB","C_DEFAULT");
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        String c_usertype = userData.getString("C_USERTYPE","C_DEFAULT");
        String c_usergender = userData.getString("C_USERGENDER","C_DEFAULT");
        String c_carid = userData.getString("C_CARID","C_DEFAULT");
        String c_userrate = userData.getString("C_USERRATE","C_DEFAULT");
        String c_userid = userData.getString("C_USERID","C_DEFAULT");
        customerImage.setImageURI(Uri.parse(c_userimage));
    }

    private void initViews() {
        customerImage = findViewById(R.id.customer_profile_profile_image);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_customer_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الصفحة الشخصية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CustomerProfile.this, R.style.Theme_ResQme);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

}