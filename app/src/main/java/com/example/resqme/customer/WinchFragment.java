package com.example.resqme.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.InternetConnection;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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


        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();

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
                                Toast.makeText(context, "لا توجد اوناش متاحة الآن.", Toast.LENGTH_SHORT).show();
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
            });

        } else {
            Toast.makeText(getContext(), "بيانات الموقع غير متاحة.", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void showingDataOnTheMap(ArrayList<Winch> winchesList, String myLat, String myLong, int changed) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.clear();
                googleMapObj = googleMap;
                LatLng me = new LatLng(Double.valueOf(myLat), Double.valueOf(myLong));
                googleMap.addMarker(new MarkerOptions()
                        .position(me)
                        .title("موقعك الحالي")).showInfoWindow();

                float zoomLevel = 12.0f; //This goes up to 21
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
                GPS = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (GPS != 0) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(WinchFragment.this.getId(), new WinchFragment()).commit();
            }else{
                Toast.makeText(context, "لم يتم تفعيل بيانات الموقع.", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == 25){
            if(data!=null){
                if(!TextUtils.isEmpty(data.getStringExtra("DESC_WINCH_VALUE"))){
                    requestAttachedDescription = data.getStringExtra("DESC_WINCH_VALUE");
                    if(!TextUtils.isEmpty(requestAttachedDescription)){
                        processingWinchRequest(finalBestWinch);
                    }
                }
            }
        }else if(requestCode == 30){
            // If payment done, we can send the request
            if(data!=null){
                if(!TextUtils.isEmpty(data.getStringExtra("PAYMENT_STATUS"))){
                    PaymentStatusArg = data.getStringExtra("PAYMENT_STATUS");
                    if(!TextUtils.isEmpty(PaymentStatusArg) && PaymentStatusArg.equals("SUCCESS_P_RESQME")){
                        // Going to processing winch request page to get the description
                        Intent wpr = new Intent(getActivity(), ProcessingRequestWinch.class);
                        startActivityForResult(wpr, 25);
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
                    Toast.makeText(context, "عفواً، لطلب ونش يجب توافر إتصال بالإنترنت.", Toast.LENGTH_LONG).show();
                }else{
                    if(winchesList.size() > 0){
                        requestingWinch(view);
                    }else{
                        Toast.makeText(context, "عذراً، الخدمة غير متاحة حالياً.", Toast.LENGTH_SHORT).show();
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
        if(!carLocalData.contains("CAR_ID")){
            // Car not added yet.
            Toast.makeText(context, "لم تقم بإضافة عربية حتى الآن.", Toast.LENGTH_SHORT).show();
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
                TextView winchServiceTimeInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_service_time_txt);

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

                winchServiceDistanceInBottomSheet.setText("• المسافة التقريبية "+ (int)Math.round(Double.valueOf(bestDistance)) + " متر.");
                int time = (((int)Math.round(Double.valueOf(bestDistance)) / 17) / 60);
                if(time < 5){
                    time = 5;
                }
                winchServiceTimeInBottomSheet.
                        setText("• الزمن المتوقع لوصول الونش "+ time + " دقيقة.");

                winchRequestServiceCost = serviceCost;
                winchNameInBottomSheet.setText(bestWinch.getWinchName());
                winchServiceCostInBottomSheet.setText("تكلفة الخدمة " + serviceCost  + " جنيه.");
                finalBestWinch = bestWinch;
                winchSheetView.findViewById(R.id.btnSheet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Payment, then init the request
                        Intent paymentIntent = new Intent(getActivity(), CustomerWinchPayment.class);
                        paymentIntent.putExtra("SERVICE_COST", serviceCost);
                        startActivityForResult(paymentIntent, 30);
                    }
                });
                winchBottomDialog.setContentView(winchSheetView);
                winchBottomDialog.show();
                progressBar.setVisibility(View.GONE);
            }else if(c_userid.equals(car_user_id) && car_status.equals("Pending")){
                Toast.makeText(context, "العربية لم يتم قبولها حتى الآن، برجاء المحاولة في وقت لاحق أو تواصل معنا.", Toast.LENGTH_SHORT).show();
            }else if(c_userid.equals(car_user_id) && car_status.equals("Refused")){
                Toast.makeText(context, "تم رفض بيانات العربية، تواصل معنا لمعرفة المزيد.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void processingPayment(){

    }

    private void processingWinchRequest(Winch finalBestWinch) {
        // If customer added description, initiate the request.
        if(!TextUtils.isEmpty(requestAttachedDescription)){
            // Initiate the request
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("جاري إرسال البيانات..");
            progressDialog.show();
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
            Toast.makeText(context, "تم إرسال الطلب، يمكن متابعته في صفحة الطلبات الخاصة بك.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            winchBottomDialog.cancel();
        }
    }
}