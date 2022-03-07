package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfile extends AppCompatActivity implements View.OnClickListener{
    CircleImageView customerImage;
    TextView usernameTV, emailTV, addressTV, DOBTV, whatsAppTV, genderTV, userTypeTV, rateTV;
    Button addCarBtn, logoutBtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        mAuth = FirebaseAuth.getInstance();
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
        usernameTV.setText(c_username);
        emailTV.setText(c_email);
        addressTV.setText(c_address);
        DOBTV.setText(c_dob);
        whatsAppTV.setText(c_whatsapp);
        genderTV.setText(c_usergender);
        userTypeTV.setText(c_usertype);
        rateTV.setText(c_userrate);
        if(!c_carid.equals("0")){
            addCarBtn.setVisibility(View.GONE);
        }
    }

    private void initViews() {

        customerImage = findViewById(R.id.customer_profile_profile_image);
        usernameTV = findViewById(R.id.username_profile_text);
        emailTV = findViewById(R.id.email_profile_text);
        addressTV = findViewById(R.id.address_profile_text);
        DOBTV = findViewById(R.id.dob_profile_text);
        whatsAppTV = findViewById(R.id.whatsapp_profile_text);
        genderTV = findViewById(R.id.gender_profile_text);
        userTypeTV = findViewById(R.id.userType_profile_text);
        rateTV = findViewById(R.id.userRate_profile_text);
        addCarBtn = findViewById(R.id.add_car_data_button);
        addCarBtn.setOnClickListener(this);
        logoutBtn = findViewById(R.id.logout_customer_profile);
        logoutBtn.setOnClickListener(this);

    }

    void sendToLogin() {
        Intent loginIntent = new Intent(CustomerProfile.this, Login.class);
        startActivity(loginIntent);
        finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_car_data_button:
                Toast.makeText(this, "Open new page for adding car data!!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout_customer_profile:
                mAuth.signOut();
                sendToLogin();
                finish();
                break;
        }
    }
}