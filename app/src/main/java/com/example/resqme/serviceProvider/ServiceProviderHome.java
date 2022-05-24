package com.example.resqme.serviceProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.common.Login;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.customer.CustomerProfile;
import com.example.resqme.customer.SparePartsAdapter;
import com.example.resqme.model.CMC;
import com.example.resqme.model.Report;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.Winch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderHome extends AppCompatActivity  {
    Context context;
    CircleImageView spHomeImage;
    int GPS = 0;
    int GrantedToWork = 0;
    final boolean[] LocationPermission = {false};
    String myLat = "", myLong = "";
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest = null;
    LocationCallback locationCallback = null;
    TextView SPNAMEWelcome;
    MaterialCardView settingsMCV, cmcMCV, sparepartsMCV, winchMCV, addWinchMCV, addSparePartsMCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        context = getApplicationContext();
        initviews();
        DatabaseReference winchesData = FirebaseDatabase.getInstance().getReference().child("Winches");
        pageDataLoading();

        // Location Work for Winch and CMC only
        // Condition to be added here
        // GPS
        try {
            GPS = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (GPS == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("إعدادات الموقع");
            alertDialog.setMessage("الـ GPS غير مُفعل، لإستخدام التطبيق يجب تفعيله هل انت موافق؟");
            alertDialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(onGPS, 150);
                }
            });
            alertDialog.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        // Location
        Dexter.withContext(context).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                LocationPermission[0] = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        if (GPS != 0 && LocationPermission[0]) {
            GrantedToWork = 1;
        }
        if (GrantedToWork != 0) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20000);
            locationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                         if (location != null) {

                             myLat = String.valueOf(location.getLatitude());
                             myLong = String.valueOf(location.getLongitude());
                             // Change winch dimensions of this service provider
                             SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                             String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
                             winchesData.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                         Winch winch = dataSnapshot.getValue(Winch.class);
                                         if(winch.getWinchOwnerID().equals(sp_userid)){
                                             winchesData.child(winch.getWinchID()).child("winchCurrentLat").setValue(myLat);
                                             winchesData.child(winch.getWinchID()).child("winchCurrentLong").setValue(myLong);
                                         }
                                     }
                                 }
                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });

                         }
                    }
                }
            };
        }

        // Material Card Clicks [will appear based on the condition]

        cmcMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(context, ServiceProviderHome_CMC.class);
                startActivity(go);
            }
        });

        sparepartsMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(context, ServiceProviderHome_SpareParts.class);
                startActivity(go);
            }
        });

        winchMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(context, ServiceProviderHome_Winch.class);
                startActivity(go);
            }
        });

        settingsMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(context, ServiceProviderSettings.class);
                startActivity(goToSettings);
                LogData.saveLog("APP_CLICK","","","CLICK ON SETTINGS PAGE", "SERVICE_PROVIDER_HOME");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void pageDataLoading() {
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_email = userData.getString("SP_EMAIL","SP_DEFAULT");
        String sp_username = userData.getString("SP_USERNAME","SP_DEFAULT");
        String sp_password = userData.getString("SP_PASSWORD","SP_DEFAULT");
        String sp_address = userData.getString("SP_ADDRESS","SP_DEFAULT");
        String sp_whatsapp = userData.getString("SP_WHATSAPP","SP_DEFAULT");
        String sp_dob = userData.getString("SP_DOB","SP_DEFAULT");
        String sp_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        String sp_usertype = userData.getString("SP_USERTYPE","SP_DEFAULT");
        String sp_usergender = userData.getString("SP_USERGENDER","SP_DEFAULT");
        String sp_userrate = userData.getString("SP_USERRATE","SP_DEFAULT");
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        String sp_serviceType = userData.getString("SP_ServiceType","SP_DEFAULT");
        String sp_cmc = userData.getString("SP_CMC","SP_DEFAULT");
        String sp_spareParts = userData.getString("SP_SPARE_PARTS","SP_DEFAULT");
        String sp_winch = userData.getString("SP_WINCH","SP_DEFAULT");
        Glide.with(this).load(sp_userimage).into(spHomeImage);

        SPNAMEWelcome = findViewById(R.id.sp_name_home_txt);
        SPNAMEWelcome.setText("مرحباً "+sp_username);
        if(sp_serviceType.equals("Winch") || sp_winch.equals("True")){
            // Showing the winch data
            // Allow to change winch status to not available
            winchMCV.setVisibility(View.VISIBLE);
        }
        if(sp_serviceType.equals("SpareParts") || sp_spareParts.equals("True")){
            // Show the spare parts (Recycler View)
            // Allow to add more spare parts
            // Allow to change each spare item availability
            sparepartsMCV.setVisibility(View.VISIBLE);
        }
        if(sp_serviceType.equals("CMC")){
            //Show the CMC
            //Show Winch if found
            //Show Spare Parts if found
            //Allow to add one winch
            //Allow to add different spare parts
            //Allow to change CMC, Winch, Spare Parts Availability
            cmcMCV.setVisibility(View.VISIBLE);
            DatabaseReference serviceProviders = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
            serviceProviders.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                        if(serviceProvider.getUserId().equals(sp_userid)){
                            // If service type is CMC (default in this case)
                            // Check if isWinch is True -> Show winch only, can not add winch
                            // Check if isWinch is false -> show winch and can add one
                            // Check if isSpareParts True -> Show spare parts, can add more
                            // Check if isSpareParts is false -> show spare parts and can add spare parts
                            if(serviceProvider.getServiceType().equals("CMC")
                            && serviceProvider.getIsWinch().equals("True")){
                                winchMCV.setVisibility(View.VISIBLE);
                                addWinchMCV.setVisibility(View.GONE);

                                SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("SP_WINCH", "True");
                                editor.apply();
                            }
                            if(serviceProvider.getServiceType().equals("CMC")
                                    && serviceProvider.getIsSpareParts().equals("True")){
                                addSparePartsMCV.setVisibility(View.VISIBLE);
                                SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("SP_SPARE_PARTS", "True");
                                editor.apply();
                            }

                            if(serviceProvider.getServiceType().equals("CMC")
                            && !serviceProvider.getIsWinch().equals("True")){
                                // Show winch data and allow to add one winch
                                winchMCV.setVisibility(View.GONE);
                                addWinchMCV.setVisibility(View.VISIBLE);
                            }
                            if(serviceProvider.getServiceType().equals("CMC")
                            && !serviceProvider.getIsSpareParts().equals("True")){
                                // Show spare parts and allow to add more items
                                addSparePartsMCV.setVisibility(View.VISIBLE);
                                sparepartsMCV.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        // Button Clicks
        spHomeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceProviderHome.this, ServiceProviderProfile.class);
                startActivity(intent);
                LogData.saveLog("APP_CLICK","","","CLICK ON PROFILE PAGE", "SERVICE_PROVIDER_HOME");
            }
        });
        addWinchMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddWinchintent = new Intent(context, AddWinchData.class);
                toAddWinchintent.putExtra("FROM", "SPHOME");
                startActivity(toAddWinchintent);
            }
        });
        addSparePartsMCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddsparepartintent = new Intent(context, AddSpareParts.class);
                if(sp_serviceType.equals("CMC")){
                    toAddsparepartintent.putExtra("FROM", "SPHOME");
                }
                startActivity(toAddsparepartintent);
            }
        });
    }

    private void initviews() {
        spHomeImage = findViewById(R.id.service_provider_home_image);
        settingsMCV = findViewById(R.id.sp_home_settings_btn);
        cmcMCV = findViewById(R.id.sp_home_cmc_btn);
        sparepartsMCV = findViewById(R.id.sp_home_spare_parts_btn);
        winchMCV = findViewById(R.id.sp_home_winch_btn);
        addWinchMCV = findViewById(R.id.sp_home_add_winch_layout_btn);
        addSparePartsMCV = findViewById(R.id.sp_home_add_spare_parts_layout_btn);
    }
}
