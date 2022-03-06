package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.example.resqme.model.Customer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHome extends AppCompatActivity implements View.OnClickListener{
    Button button;
    private FirebaseAuth mAuth;
    ImageView customerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        initViews();
        forceRTLIfSupported();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WinchFragment()).commit();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendToLogin();
                finish();
            }
        });

    }


    void initViews(){
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logouthomec);
        customerProfile = findViewById(R.id.customer_home_image);
        customerProfile.setOnClickListener(this);
        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        customerProfile.setImageURI(Uri.parse(c_userimage));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.winch:
                            selectedFragment = new WinchFragment();
                            break;
                        case R.id.spare_parts:
                            selectedFragment = new SpareFragment();
                            break;
                        case R.id.cmc:
                            selectedFragment = new CMCFragment();
                            break;
                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    void sendToLogin() {
        Intent loginIntent = new Intent(CustomerHome.this, Login.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.customer_home_image:
                Intent loginIntent = new Intent(CustomerHome.this, CustomerProfile.class);
                startActivity(loginIntent);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }




}
//        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
//        String c_email = userData.getString("C_EMAIL","C_DEFAULT");
//        String c_username = userData.getString("C_USERNAME","C_DEFAULT");
//        String c_password = userData.getString("C_PASSWORD","C_DEFAULT");
//        String c_address = userData.getString("C_ADDRESS","C_DEFAULT");
//        String c_whatsapp = userData.getString("C_WHATSAPP","C_DEFAULT");
//        String c_dob = userData.getString("C_DOB","C_DEFAULT");
//        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
//        String c_usertype = userData.getString("C_USERTYPE","C_DEFAULT");
//        String c_usergender = userData.getString("C_USERGENDER","C_DEFAULT");
//        String c_carid = userData.getString("C_CARID","C_DEFAULT");
//        String c_userrate = userData.getString("C_USERRATE","C_DEFAULT");
//        String c_userid = userData.getString("C_USERID","C_DEFAULT");