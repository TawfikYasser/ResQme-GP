package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.InternetConnection;
import com.example.resqme.common.LogData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerHome extends AppCompatActivity implements View.OnClickListener{
    CircleImageView customerProfile;
    TextView headerTV;
    InternetConnection ic;
    MaterialButton orders, cart;
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
            headerTV.setText("ونش");
        }
        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        Glide.with(this).load(c_userimage).into(customerProfile);

        ic = new InternetConnection(this);
        if(!ic.checkInternetConnection()){
            Snackbar.make(CustomerHome.this.findViewById(android.R.id.content),"لا يوجد اتصال بالإنترنت، قد لا تعمل بعض الخدمات بشكل صحيح.",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrders();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToCart = new Intent(CustomerHome.this, CartForCustomer.class);
                startActivity(intentToCart);
                LogData.saveLog("APP_CLICK","","","CLICK ON CART PAGE", "CUSTOMER_HOME");
            }
        });

    }


    void initViews(){
        customerProfile = findViewById(R.id.customer_home_image);
        customerProfile.setOnClickListener(this);
        headerTV = findViewById(R.id.customer_home_header_text);
        orders = findViewById(R.id.btn_orders_customer_home);
        cart = findViewById(R.id.btn_cart_customer_home);
    }

    private void goToOrders(){
        String[] Requests = {"طلبات الونش", "طلبات مراكز الخدمة", "طلبات قطع الغيار"};
        final String[] selectedRequestType = {"طلبات الونش"};
        new AlertDialog.Builder(CustomerHome.this)
                .setTitle("طلباتك")
                .setSingleChoiceItems(Requests, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedRequestType[0] = Requests[i];
                    }
                })
                .setPositiveButton("عرض", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedRequestType[0].equals("طلبات الونش")){
                            Intent goToWinchRequests = new Intent(CustomerHome.this, WinchRequests.class);
                            startActivity(goToWinchRequests);
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW WINCH REQUESTS PAGE", "CUSTOMER_HOME");
                        }else if(selectedRequestType[0].equals("طلبات مراكز الخدمة")){
                            Intent goToCMCRequests = new Intent(CustomerHome.this, CMCRequests.class);
                            startActivity(goToCMCRequests);
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW CMC REQUESTS PAGE", "CUSTOMER_HOME");
                        }else if(selectedRequestType[0].equals("طلبات قطع الغيار")){
                            Intent goToSpareRequests = new Intent(CustomerHome.this, SparePartsRequests.class);
                            startActivity(goToSpareRequests);
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW SPARE PARTS REQUESTS PAGE", "CUSTOMER_HOME");
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
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
                            LogData.saveLog("APP_CLICK","","","CLICK ON WINCHS PAGE", "CUSTOMER_HOME");
                            break;
                        case R.id.spare_parts:
                            selectedFragment = new SpareFragment();
                            headerTV.setText("قطع غيار");
                            LogData.saveLog("APP_CLICK","","","CLICK ON SPARE PARTS PAGE", "CUSTOMER_HOME");
                            break;
                        case R.id.cmc:
                            selectedFragment = new CMCFragment();
                            headerTV.setText("مركز خدمة سيارات");
                            LogData.saveLog("APP_CLICK","","","CLICK ON CMC PAGE", "CUSTOMER_HOME");
                            break;
                        case R.id.settings:
                            selectedFragment = new SettingsFragment();
                            headerTV.setText("الإعدادات");
                            LogData.saveLog("APP_CLICK","","","CLICK ON SETTINGS PAGE", "CUSTOMER_HOME");
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
                LogData.saveLog("APP_CLICK","","","CLICK ON PROFILE PAGE", "CUSTOMER_HOME");
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