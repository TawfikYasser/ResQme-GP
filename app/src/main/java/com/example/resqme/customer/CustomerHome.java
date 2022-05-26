package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.example.resqme.model.CMC;
import com.example.resqme.model.LogDataModel;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.WinchRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerHome extends AppCompatActivity implements View.OnClickListener{
    CircleImageView customerProfile, infoImage;
    TextView headerTV;
    InternetConnection ic;
    MaterialButton orders, cart;
    DatabaseReference logDB;
    ArrayList<LogDataModel> logs;
    ArrayList<String> times;
    ArrayList<String> spareList;
    ArrayList<String> cmcList;
    int avgTime = 0;
    String mostFrequentCarType = "", mostSupportedCarType ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        initViews();
        forceRTLIfSupported();
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        logs = new ArrayList<>();
        times = new ArrayList<>();
        spareList = new ArrayList<>();
        cmcList = new ArrayList<>();
        infoImage = findViewById(R.id.info_customer_home);
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

        // Winch Request AVG Time
        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(logData.getEventType().equals("SERVICE_CLICK") && logData.getServiceName().equals("WINCH")){
                        logs.add(logData);
                    }
                }

                DatabaseReference winchRequests = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                winchRequests.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                            // itervate over logs and check if the winch request is in the logs
                            for (LogDataModel logData : logs) {
                                if(logData.getServiceID().equals(winchRequest.getWinchID())){
                                    // Calculate the time difference between the request and the log
                                    // convert string to date
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date logDate = null, requestDate = null;
                                    try {
                                        logDate = sdf.parse(logData.getLogTimestamp());
                                        requestDate = sdf.parse(winchRequest.getWinchRequestInitiationDate());
                                    }
                                    catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    // Get the difference in milliseconds
                                    long diff = requestDate.getTime()- logDate.getTime();
                                    // Convert to minutes
                                    long diffMinutes = diff / (60 * 1000) % 60;
                                    times.add(String.valueOf(diffMinutes));
                                }
                            }
                        }
                        if(times.size() > 0){
                            // Calculate the average time
                            int sum = 0;
                            for (String time : times) {
                                sum += Integer.parseInt(time);

                            }
                            int average = sum / times.size();
                            methodToProcess(average);
                            infoImage.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Most car type from spare parts
        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(logData.getServiceName().equals("SPARE_PARTS")){
                        DatabaseReference sparePartsDB = FirebaseDatabase.getInstance().getReference("SpareParts");
                        sparePartsDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    SparePart spareParts = dataSnapshot.getValue(SparePart.class);
                                    if(spareParts.getItemID().equals(logData.getServiceID())){
                                        spareList.add(spareParts.getItemCarType());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                // Most frequent item in spareList
                if(spareList.size() > 0){
                    int max = 0;
                    String mostFrequent = "";
                    for (String item : spareList) {
                        int count = Collections.frequency(spareList, item);
                        if(count > max){
                            max = count;
                            mostFrequent = item;

                        }

                    }
                    carMostType(mostFrequent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Most car supported type from cmc
        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(logData.getServiceName().equals("CMC")){
                        DatabaseReference sparePartsDB = FirebaseDatabase.getInstance().getReference("CMCs");
                        sparePartsDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    CMC cmc = dataSnapshot.getValue(CMC.class);
                                    if(cmc.getCmcID().equals(logData.getServiceID())){
                                        cmcList.add(cmc.getCmcBrand());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                // Most frequent item in cmcList
                if(cmcList.size() > 0){
                    int max = 0;
                    String mostFrequent = "";
                    for (String item : cmcList) {
                        int count = Collections.frequency(cmcList, item);
                        if(count > max){
                            max = count;
                            mostFrequent = item;

                        }

                    }
                    carSupportedMostType(mostFrequent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(headerTV.getText().equals("ونش")){
                    if(avgTime != 0){
                        final Dialog dialog = new Dialog(CustomerHome.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.info_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(true);
                        dialog.findViewById(R.id.info_dialog_text);
                        dialog.show();
                        TextView textInDialog = (TextView) dialog.findViewById(R.id.info_dialog_text);

                        if(avgTime == 2){
                            textInDialog.setText("هل تعلم أن عملية طلب الونش لا تزيد عن دقيقتين؟ أطلب الآن في أسرع وقت");
                        }else if(avgTime == 3 || avgTime == 4|| avgTime == 5 || avgTime == 6 || avgTime == 7 || avgTime == 8 || avgTime == 9 || avgTime == 10){
                            textInDialog.setText("هل تعلم أن عملية طلب الونش لا تزيد عن "+avgTime+" دقائق؟ أطلب الآن في أسرع وقت");
                        }else{
                            textInDialog.setText("هل تعلم أن عملية طلب الونش لا تزيد عن "+avgTime+" دقيقة؟ أطلب الآن في أسرع وقت");
                        }
                    }
                }else if(headerTV.getText().equals("قطع غيار")){
                    if(!mostFrequentCarType.equals("")){
                        final Dialog dialog = new Dialog(CustomerHome.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.info_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(true);
                        dialog.findViewById(R.id.info_dialog_text);
                        dialog.show();
                        TextView textInDialog = (TextView) dialog.findViewById(R.id.info_dialog_text);
                        textInDialog.setText("هل تعلم أن أكثر قطع الغيار طلباً هو لعربيات "+mostFrequentCarType);
                    }
                }else if(headerTV.getText().equals("مركز خدمة سيارات")){
                    if(!mostSupportedCarType.equals("")){
                        final Dialog dialog = new Dialog(CustomerHome.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.info_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(true);
                        dialog.findViewById(R.id.info_dialog_text);
                        dialog.show();
                        TextView textInDialog = (TextView) dialog.findViewById(R.id.info_dialog_text);
                        textInDialog.setText("هل تعلم أن أكثر مراكز الخدمة طلباً هو لنوع العربيات الـ "+mostSupportedCarType);
                    }
                }
            }
        });

    }

    private void carSupportedMostType(String mostFrequent) {
        mostSupportedCarType = mostFrequent;
    }

    private void carMostType(String mostFrequent) {
        mostFrequentCarType = mostFrequent;
    }

    private void methodToProcess(int average) {
        avgTime = average;
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