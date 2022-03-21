package com.example.resqme.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.model.Winch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class WinchFragment extends Fragment implements View.OnClickListener {

    public WinchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Views and Variables
    private SupportMapFragment mapFragment = null;
    GoogleMap mMap;
    DatabaseReference winches;
    ArrayList<Winch> winchesList;
    MaterialButton requestWinchBtn;
    BottomSheetDialog winchBottomDialog;
    Context context;
    int GPS = 0;
    int GrantedToWork = 0;
    final boolean[] LocationPermission = {false};
    GoogleMap googleMapObj;
    String myLat = "", myLong = "";


    /*
     * This fragment procedure as the follows:
     * Check if Location & GPS are enabled
     * If they are enabled show the map and show winches
     * And get current customer location to calculate the distance
     * Between the customer and each winch
     * Else ask for the permissions and ....
     * */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_winch, container, false);
        context = container.getContext();
        winches = FirebaseDatabase.getInstance().getReference().child("Winches");
        winchesList = new ArrayList<>();
        requestWinchBtn = (MaterialButton) view.findViewById(R.id.requestWinchBTN);
        requestWinchBtn.setOnClickListener((View.OnClickListener) this);
        winchBottomDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();



        // GPS
        try {
            GPS = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (GPS == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                LocationPermission[0] = true;
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), "");
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
                    getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            myLat = String.valueOf(location.getLatitude());
                            myLong = String.valueOf(location.getLongitude());
                        }
                    });

            winches.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    winchesList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Winch winch = dataSnapshot.getValue(Winch.class);
                        if (winch.getWinchStatus().equals("Approved")) {
                            winchesList.add(winch);
                        }
                    }
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            googleMap.clear();
                            googleMapObj = googleMap;
                            LatLng me = new LatLng(Double.valueOf(myLat), Double.valueOf(myLong));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(me)
                                    .title("عنواني")).showInfoWindow();
                            //Toast.makeText(context, myLat + " - " + myLong, Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < winchesList.size(); i++) {
                                //Get each winch and pin on the map
                                Winch winch = winchesList.get(i);
                                Geocoder coder = new Geocoder(getActivity());
                                List<Address> address;
                                LatLng p1 = null;
                                try {
                                    // May throw an IOException
                                    address = coder.getFromLocationName(winch.getWinchCurrentLocation(), 5);

                                    Address location = address.get(0);
                                    p1 = new LatLng(location.getLatitude(), location.getLongitude());

                                    // Put the winch on the map
                                    LatLng latLng = new LatLng(p1.latitude, p1.longitude);
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                            .title(winch.getWinchName())
                                            .snippet(winch.getWinchCurrentLocation())
                                            .icon(BitmapFromVector(getContext(), R.drawable.winch_marker)));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    float zoomLevel = 12.0f; //This goes up to 21
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                                } catch (IOException ex) {

                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(getContext(), "Permissions Denied, Data won't be showed.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 150) {
//            try {
//                GPS = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
//            } catch (Settings.SettingNotFoundException e) {
//                e.printStackTrace();
//            }
//            Toast.makeText(context, "R" + String.valueOf(GPS), Toast.LENGTH_SHORT).show();
//            if (GPS != 0) {
//                getFragmentManager().beginTransaction().detach(WinchFragment.this).attach(WinchFragment.this).commit();
//            }
//        }
//    }

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

    // Clicking methods

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.requestWinchBTN:
                requestingWinch(view);
                break;
        }
    }

    private void requestingWinch(View view) {

        // Customer must have car and there are available winches on the map
        SharedPreferences cld = getContext().getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String customerCarID = cld.getString("C_CARID", "C_DEFAULT"); // Car must be approved also

        if (winchesList.size() > 0) {
            View winchSheetView = LayoutInflater.from(getContext()).inflate(R.layout.winch_bottom_layout,
                    (LinearLayout) view.findViewById(R.id.bottom_sheet_winch_linear_layout));

//
//                    if(customerCarID.equals("0")){
//
//                    }else{
//
//                    }



            TextView winchNameInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_name_txt);
            TextView winchServiceCostInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_service_cost_txt);

            // Recommending the best winch based on the current location of both winch and customer
            // The nearest winch to the customer is the best
            LatLng p1 = null;
            HashMap<String, Float> winchCustomerDistance = new HashMap<String, Float>();
            float[] resultDistances = new float[1];
            for(int i = 0 ; i < winchesList.size() ; i++){

                Geocoder coder = new Geocoder(getActivity());
                List<Address> address;

                try {
                    // May throw an IOException
                    address = coder.getFromLocationName(winchesList.get(i).getWinchCurrentLocation(), 5);
                    Address location = address.get(0);
                    p1 = new LatLng(location.getLatitude(), location.getLongitude());
                    Location.distanceBetween(Double.valueOf(myLat), Double.valueOf(myLong), p1.latitude, p1.longitude, resultDistances);
                    winchCustomerDistance.put(winchesList.get(i).getWinchID(), resultDistances[0]);

                } catch (IOException ex) {

                    ex.printStackTrace();
                }

            }

            List<Map.Entry<String, Float> > list =
                    new LinkedList<Map.Entry<String, Float> >(winchCustomerDistance.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Float> >() {
                public int compare(Map.Entry<String, Float> o1,
                                   Map.Entry<String, Float> o2)
                {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });


            // put data from sorted list to hashmap
            HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
            for (Map.Entry<String, Float> aa : list) {
                temp.put(aa.getKey(), aa.getValue());
            }

            String bestDistance = String.valueOf(temp.values().toArray()[0]);
            String bestId = String.valueOf(temp.keySet().toArray()[0]);

            //Toast.makeText(context, bestId + " - " + bestDistance, Toast.LENGTH_SHORT).show();

            Winch bestWinch = null;

            for(int i = 0 ; i < winchesList.size() ; i++){
                if(winchesList.get(i).getWinchID().equals(bestId)){
                    bestWinch = winchesList.get(i);
                    break;
                }
            }

//            PolylineOptions polylineOptions = new PolylineOptions()
//                    .add(new LatLng(Double.valueOf(myLat), Double.valueOf(myLong)))
//                    .add(new LatLng(p1.latitude, p1.longitude)); // Closes the polyline.
//
//            // Get back the mutable Polyline
//            Polyline polyline = googleMapObj.addPolyline(polylineOptions);

            String serviceCost = String.valueOf(

                    (int)(
                    (Double.parseDouble(bestDistance) / 1000)
                    *
                    Integer.parseInt(bestWinch.getWinchCostPerKM())
                    )
                            + 50
            );
            winchNameInBottomSheet.setText(bestWinch.getWinchName());
            winchServiceCostInBottomSheet.setText("تكلفة الخدمة " + serviceCost  + " جنيه.");

            winchSheetView.findViewById(R.id.btnSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("طلب ونش")
                            .setMessage("هل أنت متأكد من طلبك؟")
                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "سيتم ....", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("لا", null)
                            .show();
                }
            });
            winchBottomDialog.setContentView(winchSheetView);
            winchBottomDialog.show();
        }
    }

}