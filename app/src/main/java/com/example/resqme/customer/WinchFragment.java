package com.example.resqme.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.Splash;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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


public class WinchFragment extends Fragment implements View.OnClickListener{



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
    MaterialButton requestWinchBtn;
    BottomSheetDialog winchBottomDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_winch, container, false);
        checkLocationPermission();
        winches = FirebaseDatabase.getInstance().getReference().child("Winches");
        winchesList = new ArrayList<>();
        requestWinchBtn = (MaterialButton) view.findViewById(R.id.requestWinchBTN);
        requestWinchBtn.setOnClickListener((View.OnClickListener) this);
        winchBottomDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);



        if (mapFragment == null && isPermissionGranted) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();
        }

        winches.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                winchesList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Winch winch = dataSnapshot.getValue(Winch.class);
                    if(winch.getWinchStatus().equals("Approved")){
                        winchesList.add(winch);
                    }
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
                                            .snippet(winch.getWinchCurrentLocation())
                                    .icon(BitmapFromVector(getContext(), R.drawable.winch_marker)));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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

//        final Handler handler = new Handler();
//        final int delay = 5000;
//
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show(); // Do your work here
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.requestWinchBTN:
                // Customer must have car and there are available winches on the map
                SharedPreferences cld = getContext().getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                String customerCarID = cld.getString("C_CARID", "C_DEFAULT"); // Car must be approved also

                if(winchesList.size() > 0){
//
//                    if(customerCarID.equals("0")){
//
//                    }else{
//
//                    }

                    View winchSheetView = LayoutInflater.from(getContext()).inflate(R.layout.winch_bottom_layout,
                            (LinearLayout) view.findViewById(R.id.bottom_sheet_winch_linear_layout));

                    TextView winchNameInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_name_txt);
                    TextView winchServiceCostInBottomSheet = winchSheetView.findViewById(R.id.winch_bottom_sheet_service_cost_txt);

                    // Recommending the winch equation:





                    winchNameInBottomSheet.setText(winchesList.get(0).getWinchName());
                    winchServiceCostInBottomSheet.setText("تكلفة الخدمة " + winchesList.get(0).getWinchCostPerKM() + " جنيه لكل كيلومتر");

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
                break;
        }
    }
}