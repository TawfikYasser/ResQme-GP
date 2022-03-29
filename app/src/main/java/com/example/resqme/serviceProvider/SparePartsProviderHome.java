package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.customer.CMCFragment;
import com.example.resqme.customer.CustomerProfile;
import com.example.resqme.customer.SettingsFragment;
import com.example.resqme.customer.SpareFragment;
import com.example.resqme.customer.WinchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SparePartsProviderHome extends AppCompatActivity implements View.OnClickListener {
    CircleImageView sparePartsProfile;
    TextView headerTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_parts_provider_home);
        initViews();
        Locale.setDefault(new Locale("ar"));
        forceRTLIfSupported();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WinchFragment()).commit();
            headerTV.setText("مزود قطع غيار");
        }
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        Glide.with(this).load(c_userimage).into(sparePartsProfile);


    }


    void initViews(){
        sparePartsProfile = findViewById(R.id.spareParts_home_image);
        sparePartsProfile.setOnClickListener(this);
        headerTV = findViewById(R.id.spareParts_home_header_text);
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
                Intent intent = new Intent(SparePartsProviderHome.this, ServiceProviderHome.class);
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
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        Glide.with(this).load(c_userimage).into(sparePartsProfile);
    }
}