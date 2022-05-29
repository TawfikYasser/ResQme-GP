package com.example.resqme.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.DialogMessages;
import com.example.resqme.common.InternetConnection;
import com.example.resqme.common.LogData;
import com.example.resqme.model.Winch;
import com.example.resqme.model.WinchRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
    String woLat = "", woLong = "";
    String requestAttachedDescription = "";
    String PaymentStatusArg = "";
    Winch finalBestWinch = null;
    ProgressDialog progressDialog;
    String winchRequestServiceCost = "";
    ProgressBar progressBar;
    ProgressDialog progressDialogPayment;

    /*
     * This fragment works as the follows:
     * Check if Location & GPS are enabled
     * If they are enabled show the map and show winches
     * And get current customer location to calculate the distance
     * Between the customer and each winch
     * Else ask for the permissions.
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
        progressBar = (ProgressBar) view.findViewById(R.id.winchprogressmain);
        progressDialogPayment = new ProgressDialog(context);

        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();
        mapFragment.setMenuVisibility(false);
        // GPS
        try {
            GPS = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (GPS == 0) {
            progressBar.setVisibility(View.GONE);
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
                if (location.isSuccessful()) {
                    if(location.getResult() != null){
                        myLat = String.valueOf(location.getResult().getLatitude());
                        myLong = String.valueOf(location.getResult().getLongitude());
                        // Getting winches, pinning current location of the customer.
                        winches.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                winchesList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Winch winch = dataSnapshot.getValue(Winch.class);
                                    if (winch.getWinchStatus().equals("Approved") && winch.getWinchAvailability().equals("Available")) {
                                        winchesList.add(winch);
                                    }
                                }
                                if (winchesList.size() == 0) {
                                    progressBar.setVisibility(View.GONE);

                                } else {
                                    showingDataOnTheMap(winchesList, myLat, myLong, 0);
                                    requestWinchBtn.setEnabled(true);

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
                                                    showingDataOnTheMap(winchesList, String.valueOf(location.getLatitude()),
                                                            String.valueOf(location.getLongitude()), 1);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            });

        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content),"بيانات الموقع غير متاحة.",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
        return view;
    }

    private void showingDataOnTheMap(ArrayList<Winch> winchesList, String myLat, String myLong, int changed) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
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
                //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
                googleMap.setMinZoomPreference(googleMap.getCameraPosition().zoom);
                // remove any winches from the map to fill them again
                googleMap.clear();
                googleMapObj = googleMap;
                LatLng me = new LatLng(Double.valueOf(myLat), Double.valueOf(myLong));
                googleMap.addMarker(new MarkerOptions()
                        .position(me)
                        .title("موقعك الحالي")).showInfoWindow();
                for (int i = 0; i < winchesList.size(); i++) {
                    //Get each winch and pin on the map
                    Winch winch = winchesList.get(i);
                    //Converting current lat and long to address for snippet showing
                    Geocoder geocoder;
                    List<Address> addresses = null;
                    Locale locale = new Locale("ar");
                    geocoder = new Geocoder(context, locale);
                    String address = "";
                    try {
                        addresses = geocoder.getFromLocation(Double.valueOf(winch.getWinchCurrentLat()),
                                Double.valueOf(winch.getWinchCurrentLong()), 1);
                        if (addresses.size() > 0) {
                            address = addresses.get(0).getAddressLine(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Put the winch on the map
                    LatLng latLng = new LatLng(Double.valueOf(winch.getWinchCurrentLat()), Double.valueOf(winch.getWinchCurrentLong()));
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapFromVector(getContext(), R.drawable.winch_marker)));
                    progressBar.setVisibility(View.GONE);
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
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
                GPS = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (GPS != 0) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(WinchFragment.this.getId(), new WinchFragment()).commit();
            }else{
                Snackbar.make(getActivity().findViewById(android.R.id.content),"لم يتم تفعيل بيانات الموقع.",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }
        }else if(requestCode == 25) {
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("DESC_WINCH_VALUE"))) {
                    requestAttachedDescription = data.getStringExtra("DESC_WINCH_VALUE");
                    if (!TextUtils.isEmpty(requestAttachedDescription)) {
                        processingWinchRequest(finalBestWinch);
                    }
                }
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

    // Clicking methods
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.requestWinchBTN:
                InternetConnection ic;
                ic = new InternetConnection(context);
                if(!ic.checkInternetConnection()){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"عفواً، لطلب ونش يجب توافر إتصال بالإنترنت.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }else{
                    if(winchesList.size() > 0){
                        requestingWinch(view);
                    }else{
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"عذراً، الخدمة غير متاحة حالياً.",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.red_color))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                }
                break;
        }
    }

    // The following function is to handle getting the best winch for the current customer.
    // The rest of the process is in another function.
    private void requestingWinch(View view) {
        // Customer must have an approved car
        SharedPreferences userData = getActivity().getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences carLocalData = getActivity().getSharedPreferences("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
        String car_user_id = carLocalData.getString("CAR_USER_ID", "CAR_DEFAULT");
        String car_status = carLocalData.getString("CAR_STATUS", "CAR_DEFAULT");

        File carPrefFile = new File("/data/data/com.example.resqme/shared_prefs/CAR_LOCAL_DATA.xml");
        if(!carPrefFile.exists()){
            // Car not added yet.
            Snackbar.make(getActivity().findViewById(android.R.id.content),"لم تقم بإضافة عربية حتى الآن.",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }else{
            // Car added, but may be approved or not.
            if(c_userid.equals(car_user_id) && car_status.equals("Approved")){
                progressBar.setVisibility(View.VISIBLE);
                // Defining the bottom sheet view and it's components
                View winchSheetView = LayoutInflater.from(getContext()).inflate(R.layout.winch_bottom_layout,
                        (LinearLayout) view.findViewById(R.id.bottom_sheet_winch_linear_layout));

                TextView winchNameInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_name_txt);
                TextView winchServiceCostInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_service_cost_txt);
                TextView winchServiceDistanceInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_service_distance_txt);
                TextView welcometxt = winchSheetView.findViewById(R.id.winch_bottom_welcome_txt);
                welcometxt.setTextColor(getResources().getColor(R.color.purple_700));
                welcometxt.setText("مرحباً "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                // Recommending the best winch based on the current location of both winch and customer
                // The nearest winch to the customer is the best
                LatLng p1 = null;
                HashMap<String, Float> winchCustomerDistance = new HashMap<String, Float>();
                float[] resultDistances = new float[1];
                for(int i = 0 ; i < winchesList.size() ; i++){

                    p1 = new LatLng(Double.valueOf(winchesList.get(i).getWinchCurrentLat()),
                            Double.valueOf(winchesList.get(i).getWinchCurrentLong()));
                    woLat = String.valueOf(p1.latitude);
                    woLong = String.valueOf(p1.longitude);
                    Location.distanceBetween(Double.valueOf(myLat), Double.valueOf(myLong), p1.latitude, p1.longitude, resultDistances);
                    winchCustomerDistance.put(winchesList.get(i).getWinchID(), resultDistances[0]);

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
                String bestId = String.valueOf(temp.keySet().toArray()[0]);
                String bestDistance = String.valueOf(temp.values().toArray()[0]);
                Winch bestWinch = null;
                for(int i = 0 ; i < winchesList.size() ; i++){
                    if(winchesList.get(i).getWinchID().equals(bestId)){
                        bestWinch = winchesList.get(i);
                        break;
                    }
                }
                String serviceCost = String.valueOf(
                        (int)(
                                (Double.parseDouble(bestDistance) / 1000)
                                        *
                                Integer.parseInt(bestWinch.getWinchCostPerKM())
                        )
                                + 50
                );

                if((int)Math.round(Double.valueOf(bestDistance)) > 1000){
                    // convert meters to km
                    bestDistance = String.valueOf(Double.valueOf(bestDistance) / 1000);
                    winchServiceDistanceInBottomSheet.setText("• المسافة تقريباً "+ new DecimalFormat("##.##").format(Double.valueOf(bestDistance)) + " كيلو متر.");
                }else{
                    winchServiceDistanceInBottomSheet.setText("• المسافة تقريباً "+ (int)Math.round(Double.valueOf(bestDistance)) + " متر.");
                }


                winchRequestServiceCost = serviceCost;
                winchNameInBottomSheet.setText(bestWinch.getWinchName());
                winchServiceCostInBottomSheet.setText("• تكلفة الخدمة " + serviceCost  + " جنيه.");
                finalBestWinch = bestWinch;


                winchSheetView.findViewById(R.id.btnSheet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Going to processing winch request page to get the description
                        Intent wpr = new Intent(getActivity(), ProcessingRequestWinch.class);
                        wpr.putExtra("PAYMENT_COST",serviceCost);
                        startActivityForResult(wpr, 25);
                        LogData.saveLog("SERVICE_CLICK",finalBestWinch.getWinchID(),"WINCH","", "WINCH");
                    }
                });
                winchBottomDialog.setContentView(winchSheetView);
                winchBottomDialog.show();
                progressBar.setVisibility(View.GONE);
            }else if(c_userid.equals(car_user_id) && car_status.equals("Pending")){
                Snackbar.make(getActivity().findViewById(android.R.id.content),"العربية لم يتم قبولها حتى الآن، برجاء المحاولة في وقت لاحق أو تواصل معنا.",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }else if(c_userid.equals(car_user_id) && car_status.equals("Refused")){
                Snackbar.make(getActivity().findViewById(android.R.id.content),"تم رفض بيانات العربية، تواصل معنا لمعرفة المزيد.",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }
        }
    }

    
    private void processingWinchRequest(Winch finalBestWinch) {
        // If customer added description, initiate the request.
        if(!TextUtils.isEmpty(requestAttachedDescription)){
            // Initiate the request
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("جاري إرسال البيانات..");
            progressDialog.show();
            progressDialog.setCancelable(false);
            DatabaseReference winchRequestDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
            FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
            String winchRequestID = databaseRef.getReference("WinchRequests").push().getKey();

            Date currentTime = Calendar.getInstance().getTime();
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(currentTime);
            String requestTimestamp = date;

            SharedPreferences userData = getActivity().getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
            String c_userid = userData.getString("C_USERID", "C_DEFAULT");
            String c_car_id = userData.getString("C_CARID", "C_DEFAULT");
            WinchRequest winchRequest = new WinchRequest(winchRequestID, c_userid, myLat, myLong, c_car_id, finalBestWinch.getWinchOwnerID(),
                     woLat, woLong, finalBestWinch.getWinchID(), winchRequestServiceCost, requestAttachedDescription, requestTimestamp, "Pending");

            winchRequestDB.child(winchRequestID).setValue(winchRequest);

            //Marking the winch as un available
            DatabaseReference winches = FirebaseDatabase.getInstance().getReference().child("Winches");
            winches.child(finalBestWinch.getWinchID()).child("winchAvailability").setValue("Not Available");

            Snackbar.make(getActivity().findViewById(android.R.id.content),"تم إرسال الطلب، يمكن متابعته في صفحة الطلبات الخاصة بك.",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.blue_back))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            progressDialog.dismiss();
            winchBottomDialog.cancel();
            DialogMessages.showSuccessDialogWinchRequest(getActivity());
        }
    }
}