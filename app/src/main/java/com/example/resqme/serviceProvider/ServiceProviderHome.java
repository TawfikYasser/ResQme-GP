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
import com.example.resqme.common.Login;
import com.example.resqme.common.MyReportAdapter;
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
    LinearLayout cmc_HOME;
    LinearLayout sp_HOME;
    LinearLayout winch_HOME;
    Context context;
    String Type;
    CircleImageView spHomeImage;
    MaterialButton goToSettingsBtnFromSPHome;
    int GPS = 0;
    int GrantedToWork = 0;
    final boolean[] LocationPermission = {false};
    String myLat = "", myLong = "";
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest = null;
    LocationCallback locationCallback = null;


    // Winch Data
    ImageView winchLicenceImageSPHome;
    TextView winchNameSPHome, winchStatusSPHome, winchAvailabilitySPHome, winchCostPerKMSPHome;
    MaterialButton changeWinchAvailabilityBTN;
    String winchAvailability = "", winchID = "";


    // Spare Parts Data
    RecyclerView sparePartsSPHomeRV;
    DatabaseReference sparePartsDB;
    SparePartsSPHomeAdapter SparePartsAdapter;
    ArrayList<SparePart> spareParts;
    MaterialButton addSparePartsSPHome;


    // CMC Data
    ImageView cmcImageSPHome;
    TextView cmcNameSPHome, cmcLocationSPHome, cmcStatusSPHome, cmcAvailabilitySPHome, cmcBrandSPHome;
    MaterialButton changeCMCAvailability, cmcAddWinch;
    String cmcAvailability = "", cmcID = "";
    MaterialCardView winchCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        context = getApplicationContext();
        initviews();
        pageDataLoading();

        DatabaseReference winchesData = FirebaseDatabase.getInstance().getReference().child("Winches");

        // Location Work
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
            locationRequest.setInterval(10000);
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

        goToSettingsBtnFromSPHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(context, ServiceProviderSettings.class);
                startActivity(goToSettings);
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


        if(sp_serviceType.equals("Winch") || sp_winch.equals("True")){
            // Showing the winch data
            // Allow to change winch status to not available
            winch_HOME.setVisibility(View.VISIBLE);
            DatabaseReference winchesTable = FirebaseDatabase.getInstance().getReference().child("Winches");
            winchesTable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Winch winch = dataSnapshot.getValue(Winch.class);
                        if(winch.getWinchOwnerID().equals(sp_userid)){
                            // This is the right winch
                            // Fill the data
                            winchID = winch.getWinchID();
                            Glide.with(context).load(winch.getWinchLicence()).into(winchLicenceImageSPHome);
                            winchNameSPHome.setText(winch.getWinchName());
                            winchCostPerKMSPHome.setText(winch.getWinchCostPerKM() + " جنيه لكل كيلو متر.");
                            if(winch.getWinchAvailability().equals("Available")){
                                winchAvailabilitySPHome.setText("متاح");
                                winchAvailabilitySPHome.setTextColor(Color.GREEN);
                                changeWinchAvailabilityBTN.setText("اجعل الونش غير متاح");
                                winchAvailability = "Available";
                            }else{
                                winchAvailabilitySPHome.setText("غير متاح");
                                winchAvailabilitySPHome.setTextColor(Color.RED);
                                changeWinchAvailabilityBTN.setText("اجعل الونش متاح");
                                winchAvailability = "Not Available";
                            }
                            if(winch.getWinchStatus().equals("Pending")){
                                changeWinchAvailabilityBTN.setEnabled(false);
                                winchAvailabilitySPHome.setText("غير متاح");
                                winchAvailabilitySPHome.setTextColor(Color.RED);

                                winchStatusSPHome.setText("يتم مراجعة بيانات الونش");
                                winchStatusSPHome.setTextColor(Color.rgb(255, 166, 53));
                            }else if(winch.getWinchStatus().equals("Approved")){
                                changeWinchAvailabilityBTN.setEnabled(true);
                                winchStatusSPHome.setText("تم قبول الونش");
                                winchStatusSPHome.setTextColor(Color.GREEN);
                            }else if(winch.getWinchStatus().equals("Refused")){
                                winchAvailabilitySPHome.setText("غير متاح");
                                winchAvailabilitySPHome.setTextColor(Color.RED);

                                changeWinchAvailabilityBTN.setEnabled(false);
                                winchStatusSPHome.setText("تم رفض الونش");
                                winchStatusSPHome.setTextColor(Color.RED);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        if(sp_serviceType.equals("SpareParts") || sp_spareParts.equals("True")){
            // Show the spare parts (Recycler View)
            // Allow to add more spare parts
            // Allow to change each spare item availability
            sp_HOME.setVisibility(View.VISIBLE);
            sparePartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
            sparePartsSPHomeRV.setHasFixedSize(true);
            sparePartsSPHomeRV.setLayoutManager(new LinearLayoutManager(this));
            spareParts = new ArrayList<>();
            SparePartsAdapter = new SparePartsSPHomeAdapter(context, spareParts);
            sparePartsSPHomeRV.setAdapter(SparePartsAdapter);

            sparePartsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    spareParts.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                        if(sparePart.getItemServiceProviderId().equals(sp_userid)){
                            spareParts.add(sparePart);
                            SparePartsAdapter = new SparePartsSPHomeAdapter(context, spareParts);
                            sparePartsSPHomeRV.setAdapter(SparePartsAdapter);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if(sp_serviceType.equals("CMC")){
            //Show the CMC
            //Show Winch if found
            //Show Spare Parts if found
            //Allow to add one winch
            //Allow to add different spare parts
            //Allow to change CMC, Winch, Spare Parts Availability
            cmc_HOME.setVisibility(View.VISIBLE);
            DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
            cmcTable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CMC cmc = dataSnapshot.getValue(CMC.class);
                        if(cmc.getCmcServiceProviderId().equals(sp_userid)){
                            cmcID = cmc.getCmcID();
                            Glide.with(context).load(cmc.getCmcImage()).into(cmcImageSPHome);
                            cmcNameSPHome.setText(cmc.getCmcName());
                            cmcLocationSPHome.setText(cmc.getCmcLocation());
                            cmcBrandSPHome.setText(cmc.getCmcBrand());
                            if(cmc.getCmcAvailability().equals("Available")){
                                cmcAvailabilitySPHome.setText("متاح");
                                cmcAvailabilitySPHome.setTextColor(Color.GREEN);
                                changeCMCAvailability.setText("اجعل مركز الخدمة غير متاح");
                                cmcAvailability = "Available";
                            }else{
                                cmcAvailabilitySPHome.setText("غير متاح");
                                cmcAvailabilitySPHome.setTextColor(Color.RED);
                                changeCMCAvailability.setText("اجعل مركز الخدمة متاح");
                                cmcAvailability = "Not Available";
                            }

                            if(cmc.getCmcStatus().equals("Pending")){
                                changeCMCAvailability.setEnabled(false);
                                cmcAvailabilitySPHome.setText("غير متاح");
                                cmcAvailabilitySPHome.setTextColor(Color.RED);

                                cmcStatusSPHome.setText("يتم مراجعة بيانات مركز الخدمة");
                                cmcStatusSPHome.setTextColor(Color.rgb(255, 166, 53));
                            }else if(cmc.getCmcStatus().equals("Approved")){
                                changeCMCAvailability.setEnabled(true);
                                cmcStatusSPHome.setText("تم قبول مركز الخدمة");
                                cmcStatusSPHome.setTextColor(Color.GREEN);
                            }else if(cmc.getCmcStatus().equals("Refused")){
                                cmcAvailabilitySPHome.setText("غير متاح");
                                cmcAvailabilitySPHome.setTextColor(Color.RED);

                                changeCMCAvailability.setEnabled(false);
                                cmcStatusSPHome.setText("تم رفض مركز الخدمة");
                                cmcStatusSPHome.setTextColor(Color.RED);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
                                winch_HOME.setVisibility(View.VISIBLE);
                                cmcAddWinch.setVisibility(View.GONE);

                                SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("SP_WINCH", "True");
                                editor.apply();
                            }
                            if(serviceProvider.getServiceType().equals("CMC")
                                    && serviceProvider.getIsSpareParts().equals("True")){
                                SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("SP_SPARE_PARTS", "True");
                                editor.apply();
                            }

                            if(serviceProvider.getServiceType().equals("CMC")
                            && !serviceProvider.getIsWinch().equals("True")){
                                // Show winch data and allow to add one winch
                                winch_HOME.setVisibility(View.VISIBLE);
                                winchCV.setVisibility(View.GONE);
                                cmcAddWinch.setVisibility(View.VISIBLE);
                            }
                            if(serviceProvider.getServiceType().equals("CMC")
                            && !serviceProvider.getIsSpareParts().equals("True")){
                                // Show spare parts and allow to add more items
                                sp_HOME.setVisibility(View.VISIBLE);
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


        // Changing winch availability
        changeWinchAvailabilityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(winchAvailability.equals("Available")){
                    // Make it not available
                    DatabaseReference winchTable = FirebaseDatabase.getInstance().getReference().child("Winches");
                    winchTable.child(winchID).child("winchAvailability").setValue("Not Available");
                }else{
                    // Make it available
                    DatabaseReference winchTable = FirebaseDatabase.getInstance().getReference().child("Winches");
                    winchTable.child(winchID).child("winchAvailability").setValue("Available");
                }
            }
        });

        cmcAddWinch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddWinchintent = new Intent(context, AddWinchData.class);
                toAddWinchintent.putExtra("FROM", "SPHOME");
                startActivity(toAddWinchintent);
            }
        });

        // Changing cmc availability
        changeCMCAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cmcAvailability.equals("Available")){
                    // Make it not available
                    DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
                    cmcTable.child(cmcID).child("cmcAvailability").setValue("Not Available");
                }else{
                    // Make it available
                    DatabaseReference cmcTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
                    cmcTable.child(cmcID).child("cmcAvailability").setValue("Available");
                }
            }
        });

        addSparePartsSPHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddsparepartintent = new Intent(context, AddSpareParts.class);
                toAddsparepartintent.putExtra("FROM", "SPHOME");
                startActivity(toAddsparepartintent);
            }
        });


        // Location work

    }

    private void initviews() {
        cmc_HOME = findViewById(R.id.cmc_linear_layout);
        sp_HOME = findViewById(R.id.spare_parts_linear_layout);
        winch_HOME = findViewById(R.id.winch_linear_layout);
        spHomeImage = findViewById(R.id.service_provider_home_image);

        winchLicenceImageSPHome = findViewById(R.id.winch_licence_image_sp_home);
        winchNameSPHome = findViewById(R.id.winch_name_item_sp_home);
        winchCostPerKMSPHome = findViewById(R.id.winch_costperkm_sp_home);
        winchStatusSPHome = findViewById(R.id.winch_status_sp_home);
        winchAvailabilitySPHome = findViewById(R.id.winch_availability_sp_home);
        changeWinchAvailabilityBTN = findViewById(R.id.change_winch_availability_sp_home_btn);

        sparePartsSPHomeRV = findViewById(R.id.spare_parts_recycler_sp_home);
        addSparePartsSPHome = findViewById(R.id.add_spare_parts_sp_home_btn);

        cmcImageSPHome = findViewById(R.id.cmc_item_image_sp_hme);
        cmcNameSPHome = findViewById(R.id.cmc_name_item_sp_home);
        cmcLocationSPHome = findViewById(R.id.cmc_location_item_sp_home);
        cmcStatusSPHome = findViewById(R.id.cmc_item_status_sp_home);
        cmcAvailabilitySPHome = findViewById(R.id.cmc_item_availability_sp_home);
        cmcBrandSPHome = findViewById(R.id.cmc_brand_item_sp_home);
        changeCMCAvailability = findViewById(R.id.change_cmc_availability_btn_sp_home);
        cmcAddWinch = findViewById(R.id.add_winch_cmc_case_only_BTN);
        winchCV = findViewById(R.id.winch_sp_home_card_view);

        goToSettingsBtnFromSPHome = findViewById(R.id.sp_home_settings_btn);
    }
}
