package com.example.resqme.customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.resqme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class WinchFragment extends Fragment implements OnMapReadyCallback {



    public WinchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private SupportMapFragment mapFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_winch, container, false);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
//            mapFragment.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap googleMap) {
//                    LatLng latLng = new LatLng(1.289545, 103.849972);
//                    googleMap.addMarker(new MarkerOptions().position(latLng)
//                            .title("Singapore"));
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                        @Override
//                        public void onMapClick(@NonNull LatLng latLng) {
//
//                        }
//                    });
//
//                }
//            });
        }
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_winchs, mapFragment).commit();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}