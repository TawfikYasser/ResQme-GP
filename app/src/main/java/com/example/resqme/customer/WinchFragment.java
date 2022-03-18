package com.example.resqme.customer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.model.Winch;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WinchFragment extends Fragment {



    public WinchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private SupportMapFragment mapFragment;
    boolean isPermissionGranted = false;
    DatabaseReference winches;
    ArrayList<Winch> winchesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_winch, container, false);
        checkLocationPermission();
        winches = FirebaseDatabase.getInstance().getReference().child("Winches");
        winchesList = new ArrayList<>();

        if (mapFragment == null && isPermissionGranted) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {

                    for(int i = 0 ; i <winchesList.size() ; i++){

                        //Get each winch and pin on the map
                        Winch winch = winchesList.get(i);
                        Geocoder coder = new Geocoder(getActivity());
                        List<Address> address;
                        LatLng p1 = null;

                        try {
                            // May throw an IOException
                            address = coder.getFromLocationName(winch.getWinchCurrentLocation(), 5);

                            Address location = address.get(0);
                            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

                            // Put the winch on the map
                            LatLng latLng = new LatLng(p1.latitude, p1.longitude);
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(winch.getWinchName())
                                    .snippet("تكلفة الخدمة " + winch.getWinchCostPerKM() + " جنيه لكل كيلومتر")
                                    .icon(BitmapFromVector(getContext(), R.drawable.winch_marker)));
                            //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            float zoomLevel = 10.0f; //This goes up to 21
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                        } catch (IOException ex) {

                            ex.printStackTrace();
                        }


                    }

                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {

                        }
                    });

                }
            });
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();
        }


        winches.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                winchesList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Winch winch = dataSnapshot.getValue(Winch.class);
                    winchesList.add(winch);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {


                            for(int i = 0 ; i <winchesList.size() ; i++){

                                //Get each winch and pin on the map
                                Winch winch = winchesList.get(i);
                                Geocoder coder = new Geocoder(getActivity());
                                List<Address> address;
                                LatLng p1 = null;

                                try {
                                    // May throw an IOException
                                    address = coder.getFromLocationName(winch.getWinchCurrentLocation(), 5);

                                    Address location = address.get(0);
                                    p1 = new LatLng(location.getLatitude(), location.getLongitude() );

                                    // Put the winch on the map
                                    LatLng latLng = new LatLng(p1.latitude, p1.longitude);
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                            .title(winch.getWinchName())
                                            .snippet("تكلفة الخدمة " + winch.getWinchCostPerKM() + " جنيه لكل كيلومتر")
                                    .icon(BitmapFromVector(getContext(), R.drawable.winch_marker)));
                                    //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    float zoomLevel = 10.0f; //This goes up to 21
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                                } catch (IOException ex) {

                                    ex.printStackTrace();
                                }


                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void checkLocationPermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

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

}