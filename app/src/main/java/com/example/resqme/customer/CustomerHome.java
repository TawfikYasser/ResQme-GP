package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
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
    ArrayList<String> spareList;
    ArrayList<String> cmcList;
    String mostFrequentCarType = "", mostSupportedCarType ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        initViews();
        forceRTLIfSupported();
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        logs = new ArrayList<>();
        spareList = new ArrayList<>();
        cmcList = new ArrayList<>();
        infoImage = findViewById(R.id.info_customer_home);
        Log.d("Login", FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    final Dialog dialog = new Dialog(CustomerHome.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.info_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setCancelable(true);
                    dialog.findViewById(R.id.info_dialog_text);
                    dialog.findViewById(R.id.contactus_whatsapp_dialog_info);
                    dialog.show();
                    TextView textInDialog = (TextView) dialog.findViewById(R.id.info_dialog_text);
                    textInDialog.setText("هل تواجه أي مشكلة في عملية طلب الونش؟ يمكنك الآن التواصل معنا عبر الواتساب حتى يمكننا مساعدتك. ❤️");
                    CircleImageView whatsAppDialog = dialog.findViewById(R.id.contactus_whatsapp_dialog_info);
                    whatsAppDialog.setVisibility(View.VISIBLE);
                    whatsAppDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            String url = "https://api.whatsapp.com/send?phone=201129348206"; // Test Link
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            LogData.saveLog("APP_CLICK","","","CLICK ON WHATSAPP ICON", "CUSTOMER_HOME");
                        }
                    });
                    LogData.saveLog("APP_CLICK","","","CLICK ON INFO DIALOG ICON FOR WINCH PAGE", "CUSTOMER_HOME");
                } else if(headerTV.getText().equals("قطع غيار")){
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
                        LogData.saveLog("APP_CLICK","","","CLICK ON INFO DIALOG ICON FOR SPARE PARTS PAGE", "CUSTOMER_HOME");
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
                        LogData.saveLog("APP_CLICK","","","CLICK ON INFO DIALOG ICON FOR CMC PAGE", "CUSTOMER_HOME");
                    }
                }else if(headerTV.getText().equals("الإعدادات")){
                    final Dialog dialog = new Dialog(CustomerHome.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.info_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setCancelable(true);
                    dialog.findViewById(R.id.info_dialog_text);
                    dialog.show();
                    TextView textInDialog = (TextView) dialog.findViewById(R.id.info_dialog_text);
                    textInDialog.setText("هل تريد تجربة خدمة الأسئلة الجديدة؟ يمكنك الآن أن تسأل سؤالك وسيجيب عليك أحد مقدمي الخدمة المختصين لمساعدتك في اختيار أفضل خدمة.");
                    MaterialButton questionBtn = (MaterialButton) dialog.findViewById(R.id.btn_info_dialog);
                    questionBtn.setVisibility(View.VISIBLE);
                    questionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent goToAskQuestionPage = new Intent(CustomerHome.this, AskQuestion.class);
                            startActivity(goToAskQuestionPage);
                            LogData.saveLog("APP_CLICK","","","CLICK ON ASK QUESTION PAGE", "CUSTOMER_HOME");
                        }
                    });
                    LogData.saveLog("APP_CLICK","","","CLICK ON INFO DIALOG ICON FOR SETTINGS PAGE", "CUSTOMER_HOME");
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
        new AlertDialog.Builder(CustomerHome.this, R.style.AlertDialogCustom)
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