package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.DialogMessages;
import com.example.resqme.common.LogData;
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProcessingRequestCMC extends AppCompatActivity {

    //CMC Data
    String cmcID_STR, cmcNameSTR, cmcAvailabilitySTR, cmcOwnerID,
            cmcCarTypeSTR, cmcImageSTR, cmcStatusSTR, cmcAddressSTR;

    Button sendDescriptionOfCMCRequest;
    TextInputEditText etCMCRequestDescription;

    DatabaseReference cmcRequestsDB;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_request_cmc);
        initToolbar();
        forceRTLIfSupported();
        cmcRequestsDB = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
        Intent intent = getIntent();
        cmcID_STR = intent.getStringExtra("CMC_ID");
        cmcNameSTR = intent.getStringExtra("CMC_NAME");
        cmcAvailabilitySTR = intent.getStringExtra("CMC_AVAILABILITY");
        cmcOwnerID = intent.getStringExtra("CMC_OWNER_ID");
        cmcCarTypeSTR = intent.getStringExtra("CMC_CARTYPE");
        cmcImageSTR = intent.getStringExtra("CMC_IMAGE");
        cmcStatusSTR = intent.getStringExtra("CMC_STATUS");
        cmcAddressSTR = intent.getStringExtra("CMC_ADDRESS");

        sendDescriptionOfCMCRequest = findViewById(R.id.send_cmc_request_btn);
        etCMCRequestDescription = findViewById(R.id.send_cmc_request_description_et);
        progressDialog = new ProgressDialog(this);


        sendDescriptionOfCMCRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etCMCRequestDescription.getText().toString().trim())){
                    new AlertDialog.Builder(ProcessingRequestCMC.this)
                            .setTitle("طلب مركز خدمة سيارات")
                            .setMessage("هل أنت متأكد من المتابعة؟")
                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendTheCMCRequest();
                                }
                            })
                            .setNegativeButton("لا", null)
                            .show();
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال وصف للطلب قبل الضغط على إرسال.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });


    }

    private void sendTheCMCRequest() {
        progressDialog.setMessage("جاري إرسال البيانات");
        progressDialog.show();
        progressDialog.setCancelable(false);
        LogData.saveLog("SERVICE_CLICK",cmcID_STR,"CMC","", "CMC_PROCESSING_REQUEST");

        //Getting current customer location

        FusedLocationProviderClient locationProviderClient = LocationServices.
                getFusedLocationProviderClient(ProcessingRequestCMC.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(location -> {
            if(location.isSuccessful()) {

                FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
                String cmcRequestID = databaseRef.getReference("CMCRequests").push().getKey();

                Date currentTime = Calendar.getInstance().getTime();
                String pattern = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(currentTime);
                String requestTimestamp = date;

                SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                String c_car_id = userData.getString("C_CARID", "C_DEFAULT");

                //Send to Firebase

                CMCRequest cmcRequest = new CMCRequest(

                        cmcRequestID, cmcID_STR, cmcNameSTR, cmcImageSTR, cmcAddressSTR, cmcAvailabilitySTR,
                        cmcStatusSTR, etCMCRequestDescription.getText().toString().trim(), "Pending", cmcOwnerID,
                        c_userid, requestTimestamp, String.valueOf(location.getResult().getLatitude()),
                        String.valueOf(location.getResult().getLongitude()), c_car_id, "CASH"

                );

                cmcRequestsDB.child(cmcRequestID).setValue(cmcRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(ProcessingRequestCMC.this.findViewById(android.R.id.content),"تم إرسال الطلب، يمكن متابعته في صفحة الطلبات الخاصة بك.",Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.blue_back))
                                    .setTextColor(getResources().getColor(R.color.white))
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                            progressDialog.dismiss();
                            DialogMessages.showSuccessDialogWinchRequest(ProcessingRequestCMC.this);
                        }else{
                            Toast.makeText(ProcessingRequestCMC.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }else{
              progressDialog.dismiss();
                Toast.makeText(this, "بيانات الموقع غير متاحة، فعلها وحاول مجدداً", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_processing_request_cmc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("إتمام الطلب");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ProcessingRequestCMC.this, R.style.Theme_ResQme);
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
}