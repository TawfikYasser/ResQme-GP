package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerHome extends AppCompatActivity implements View.OnClickListener{
    CircleImageView customerProfile;
    TextView headerTV;
    public static CustomerCart customerCart ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        customerCart = new CustomerCart();
        initViews();
        forceRTLIfSupported();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WinchFragment()).commit();
            headerTV.setText("ونش");
        }
        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        Glide.with(this).load(c_userimage).into(customerProfile);
    }

    void initViews(){
        customerProfile = findViewById(R.id.customer_home_image);
        customerProfile.setOnClickListener(this);
        headerTV = findViewById(R.id.customer_home_header_text);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.winch:
                            selectedFragment = new WinchFragment();
                            headerTV.setText("ونش");
                            break;
                        case R.id.spare_parts:
                            selectedFragment = new SpareFragment();
                            headerTV.setText("قطع غيار");
                            break;
                        case R.id.cmc:
                            selectedFragment = new CMCFragment();
                            headerTV.setText("مركز خدمة سيارات");
                            break;
                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            headerTV.setText("الإعدادات");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.customer_home_image:
                Intent intent = new Intent(CustomerHome.this, CustomerProfile.class);
                startActivity(intent);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        Glide.with(this).load(c_userimage).into(customerProfile);
    }
}