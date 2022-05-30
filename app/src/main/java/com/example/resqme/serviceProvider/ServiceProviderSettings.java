package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.resqme.R;
import com.example.resqme.common.AboutUs;
import com.example.resqme.common.ContactUs;
import com.example.resqme.common.LogData;
import com.example.resqme.common.Login;
import com.example.resqme.common.MyReports;
import com.example.resqme.common.Questions;
import com.example.resqme.customer.SendReport;
import com.example.resqme.model.CMC;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.Winch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class ServiceProviderSettings extends AppCompatActivity {
    Button winchRequests, cmcRequests, sparePartsRequests, questions, sendReports, showReports, aboutUs, contactUs, logoutBtn;
    Context context;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_settings);
        initViews();
        initToolbar();
        forceRTLIfSupported();
        context = getApplication();
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        String sp_serviceType = userData.getString("SP_ServiceType","SP_DEFAULT");
        String sp_cmc = userData.getString("SP_CMC","SP_DEFAULT");
        String sp_spareParts = userData.getString("SP_SPARE_PARTS","SP_DEFAULT");
        String sp_winch = userData.getString("SP_WINCH","SP_DEFAULT");

        if(sp_serviceType.equals("Winch")){
                sparePartsRequests.setVisibility(View.GONE);
                cmcRequests.setVisibility(View.GONE);
        }else if(sp_serviceType.equals("SpareParts")){
            winchRequests.setVisibility(View.GONE);
            cmcRequests.setVisibility(View.GONE);
        }else if(sp_serviceType.equals("CMC")){
            if(!sp_spareParts.equals("True")){
                sparePartsRequests.setVisibility(View.GONE);
            }
            if(!sp_winch.equals("True")){
                winchRequests.setVisibility(View.GONE);
            }
        }

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAboutUs = new Intent(context, AboutUs.class);
                startActivity(intentAboutUs);
                LogData.saveLog("APP_CLICK","","","CLICK ON ABOUT US PAGE", "SERVICE_PROVIDER_SETTINGS");
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentContactUs = new Intent(context, ContactUs.class);
                startActivity(intentContactUs);
                LogData.saveLog("APP_CLICK","","","CLICK ON CONTACT US PAGE", "SERVICE_PROVIDER_SETTINGS");
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ServiceProviderSettings.this, R.style.AlertDialogCustom)
                        .setTitle("تسجيل الخروج")
                        .setMessage("هل أنت متأكد انك تريد تسجيل الخروج؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LogData.saveLog("APP_CLICK","","","CLICK ON LOGOUT BUTTON", "SERVICE_PROVIDER_SETTINGS");
                                sendToLogin();
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
            }
        });


        sendReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReport();
            }
        });

        showReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyReports();
            }
        });


        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToQuestionsPage = new Intent(context, SPQuestions.class);
                startActivity(goToQuestionsPage);
                LogData.saveLog("APP_CLICK","","","CLICK ON SHOW QUESTIONS PAGE", "SERVICE_PROVIDER_SETTINGS");
            }
        });

        winchRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to = new Intent(context, SP_Winch_Requests.class);
                startActivity(to);
                LogData.saveLog("APP_CLICK","","","CLICK ON SHOW WINCH REQUESTS PAGE", "SERVICE_PROVIDER_SETTINGS");

            }
        });

        sparePartsRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to = new Intent(context, SP_Spare_Parts_Requests.class);
                startActivity(to);
                LogData.saveLog("APP_CLICK","","","CLICK ON SPARE PARTS REQUESTS PAGE", "SERVICE_PROVIDER_SETTINGS");
            }
        });

        cmcRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to = new Intent(context, SP_CMC_Requests.class);
                startActivity(to);
                LogData.saveLog("APP_CLICK","","","CLICK ON SHOW CMC REQUESTS PAGE", "SERVICE_PROVIDER_SETTINGS");
            }
        });

    }

    private void initViews() {
        winchRequests = findViewById(R.id.sp_settings_winch_requests);
        cmcRequests = findViewById(R.id.sp_settings_cmc_requests);
        sparePartsRequests = findViewById(R.id.sp_settings_spareparts_requests);
        questions = findViewById(R.id.sp_settings_questions);
        sendReports = findViewById(R.id.sp_settings_sendReport);
        showReports = findViewById(R.id.sp_settings_reports);
        aboutUs = findViewById(R.id.sp_settings_aboutUs);
        contactUs = findViewById(R.id.sp_settings_contactUs);
        logoutBtn = findViewById(R.id.sp_settings_logout);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sp_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الإعدادات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServiceProviderSettings.this, R.style.Theme_ResQme);
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
    private void sendReport() {
        Intent intent = new Intent(ServiceProviderSettings.this, SendReport.class);
        startActivity(intent);
        LogData.saveLog("APP_CLICK","","","CLICK ON ADD REPORT BUTTON", "SERVICE_PROVIDER_SETTINGS");
    }

    private void sendToMyReports() {
        Intent intent = new Intent(context, MyReports.class);
        startActivity(intent);
        LogData.saveLog("APP_CLICK","","","CLICK ON SHOW REPORTS BUTTON", "SERVICE_PROVIDER_SETTINGS");
    }

    void sendToLogin() {
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        ProgressDialog logoutDialog = new ProgressDialog(ServiceProviderSettings.this);
        logoutDialog.setMessage("جاري تسجيل الخروج...");
        logoutDialog.setCancelable(false);
        logoutDialog.show();
        DatabaseReference spareDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        spareDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                    if(sparePart.getItemServiceProviderId().equals(sp_userid)){
                        spareDB.child(sparePart.getItemID()).child("itemAvailability").setValue("Not Available");
                    }
                }

                DatabaseReference winchDB = FirebaseDatabase.getInstance().getReference().child("Winches");
                winchDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Winch winch = dataSnapshot.getValue(Winch.class);
                            if (winch.getWinchOwnerID().equals(sp_userid)) {
                                winchDB.child(winch.getWinchID()).child("winchAvailability").setValue("Not Available");
                            }
                        }

                        DatabaseReference cmcDB = FirebaseDatabase.getInstance().getReference().child("CMCs");
                        cmcDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    CMC cmc = dataSnapshot.getValue(CMC.class);
                                    if (cmc.getCmcServiceProviderId().equals(sp_userid)) {
                                        cmcDB.child(cmc.getCmcID()).child("cmcAvailability").setValue("Not Available");
                                    }
                                }
                                logoutDialog.dismiss();
                                mAuth.signOut();
                                File deletePrefFile = new File("/data/data/com.example.resqme/shared_prefs/SP_LOCAL_DATA.xml");
                                deletePrefFile.delete();
                                Intent intent = new Intent(ServiceProviderSettings.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}