package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.resqme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressMap extends AppCompatActivity implements OnMapReadyCallback{

    boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map);
        checkLocationPermission();
        if(isPermissionGranted){
            SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_map_address);
            supportMapFragment.getMapAsync(this);
        }
    }

    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "اختار عنوانك...", Toast.LENGTH_LONG).show();
        //get latlong for corners for specified place
        LatLng one = new LatLng(30.108990, 31.132619);
        LatLng two = new LatLng(29.979773, 31.287968);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //add them to builder
        builder.include(one);
        builder.include(two);
        LatLngBounds bounds = builder.build();
        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        // 10% padding
        int padding = (int) (width * 0.10);
        //set latlong bounds
        googleMap.setLatLngBoundsForCameraTarget(bounds);
        //move camera to fill the bound to screen
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        googleMap.setMinZoomPreference(googleMap.getCameraPosition().zoom);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                googleMap.clear();
                Marker choosedLocation = googleMap.addMarker(new MarkerOptions()
                .position(point).draggable(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                Geocoder geocoder;
                List<Address> addresses = null;
                Locale locale = new Locale("ar");
                geocoder = new Geocoder(AddressMap.this, locale);
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    if(addresses.size() > 0){
                        String address = addresses.get(0).getAddressLine(0);
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();
                        Toast.makeText(AddressMap.this, address, Toast.LENGTH_LONG).show();
                        Intent addressValue = new Intent();
                        addressValue.putExtra("ADDRESS_VALUE", address);
                        setResult(RESULT_OK, addressValue);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}