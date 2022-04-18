package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackingWinchRequest extends AppCompatActivity {
    private SupportMapFragment mapFragment = null;
    DatabaseReference winchesDB;
    Context context;
    int GPS = 0;
    int GrantedToWork = 0;
    final boolean[] LocationPermission = {false};
    GoogleMap googleMapObj;
    String myLat = "", myLong = "";
    ArrayList<Winch> winchArrayList;
    String winchIDSTR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_winch_request);
        Intent intent = getIntent();
        myLat = intent.getStringExtra("CUSTOMER_LAT");
        myLong = intent.getStringExtra("CUSTOMER_LONG");
        winchIDSTR = intent.getStringExtra("WINCH_ID");
        forceRTLIfSupported();
        initToolbar();
        context = getApplicationContext();
        winchArrayList = new ArrayList<>();
        winchesDB = FirebaseDatabase.getInstance().getReference().child("Winches");
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.tracking_map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng me = new LatLng(Double.valueOf(myLat), Double.valueOf(myLong));
                googleMap.addMarker(new MarkerOptions()
                        .position(me)
                        .title("موقعك الحالي")).showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(me));
                float zoomLevel = 12.0f; //This goes up to 21
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, zoomLevel));
            }
        });

        // Location & GPS
        // GPS
        try {
            GPS = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
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
                Uri uri = Uri.fromParts("package", getPackageName(), "");
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

            FusedLocationProviderClient locationProviderClient = LocationServices.
                    getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                if(location.isSuccessful()){
                    myLat = String.valueOf(location.getResult().getLatitude());
                    myLong = String.valueOf(location.getResult().getLongitude());
                    winchesDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Winch winch = dataSnapshot.getValue(Winch.class);
                                if(winch.getWinchID().equals(winchIDSTR)){
                                    winchArrayList.add(winch);
                                }
                            }
                            showTracking(winchArrayList, String.valueOf(myLat),
                                    String.valueOf(myLong), 0);
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    }
                                    googleMap.setMyLocationEnabled(true);
                                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                        @Override
                                        public void onMyLocationChange(@NonNull Location location) {
                                            myLat = String.valueOf(location.getLatitude());
                                            myLong = String.valueOf(location.getLongitude());
                                            showTracking(winchArrayList, String.valueOf(location.getLatitude()),
                                                    String.valueOf(location.getLongitude()), 1);
                                        }
                                    });
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
        }
    }

    void showTracking(ArrayList<Winch> winchesList, String myLat, String myLong, int changed){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.clear();
                float zoomLevel = 12.0f; //This goes up to 21
                googleMapObj = googleMap;
                LatLng me = new LatLng(Double.valueOf(myLat), Double.valueOf(myLong));
                googleMap.addMarker(new MarkerOptions()
                        .position(me)
                        .title("موقعك الحالي")).showInfoWindow();

                // Put the winch on the map
                LatLng latLng = new LatLng(Double.valueOf(winchesList.get(0).getWinchCurrentLat()),
                        Double.valueOf(winchesList.get(0).getWinchCurrentLong()));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapFromVector(context, R.drawable.winch_marker)));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(me));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 18.0f));
                        return true;
                    }
                });
                if(changed == 0){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(me));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 12.0f));
                }
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 150) {
            try {
                GPS = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (GPS != 0) {
                finish();
                startActivity(getIntent());
            }else{
                Toast.makeText(context, "لم يتم تفعيل بيانات الموقع.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Marker Shape
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        int height = 100;
        int width = 100;
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, 100, 100);

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_tracking_winch_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تتبع حركة الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(TrackingWinchRequest.this, R.style.Theme_ResQme);
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