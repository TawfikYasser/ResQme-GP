package com.example.resqme.customer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.model.CMC;
import com.example.resqme.model.LogDataModel;
import com.example.resqme.model.Report;
import com.example.resqme.model.SparePart;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class SpareFragment extends Fragment {

    public SpareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //InitViews
    RecyclerView sparepartsRV;
    DatabaseReference sparepartsDB;
    SparePartsAdapter sparepartsAdapter;
    ArrayList<SparePart> spareParts;
    Context context;
    ShimmerFrameLayout shimmerFrameLayoutSpareCustomer;
    ArrayList<String> sparePartsIDs;
    DatabaseReference logDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spare, container, false);
        shimmerFrameLayoutSpareCustomer = view.findViewById(R.id.spare_customer_shimmer);
        shimmerFrameLayoutSpareCustomer.startShimmer();
        sparePartsIDs = new ArrayList<>();
        sparepartsRV = view.findViewById(R.id.spare_parts_recycler);
        context = getActivity().getApplicationContext();
        sparepartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        sparepartsRV.setHasFixedSize(true);
        sparepartsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        spareParts = new ArrayList<>();
        sparepartsAdapter = new SparePartsAdapter(getActivity(), spareParts);
        sparepartsRV.setAdapter(sparepartsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutSpareCustomer.stopShimmer();
                shimmerFrameLayoutSpareCustomer.setVisibility(View.GONE);
                sparepartsRV.setVisibility(View.VISIBLE);
            }  //end of run
        }, 2500);

        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //All logic will be here
                sparePartsIDs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(logData.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            && logData.getIsService().equals("TRUE") && !logData.getClickedServiceID().isEmpty()){
                        sparePartsIDs.add(logData.getClickedServiceID());
                    }
                }
                // Count the occurrences of each clicked service ID and get the most frequent one
                HashMap<String, Integer> map = new HashMap<>();
                for (String s : sparePartsIDs) {
                    Integer count = map.get(s);
                    map.put(s, count == null ? 1 : count + 1);
                }
                String mostFrequent = "";
                int max = 0;
                for (String s : map.keySet()) {
                    if (map.get(s) > max) {
                        max = map.get(s);
                        mostFrequent = s;

                    }
                }
//                Toast.makeText(context, "The most frequent spare item: " +mostFrequent, Toast.LENGTH_SHORT).show();

                String finalMostFrequent = mostFrequent;
                sparepartsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    spareParts.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                        if(sparePart.getItemStatus().equals("Approved") && sparePart.getItemAvailability().equals("Available")){
                            // Put the spare part with the most frequent ID in the first position
                            if(sparePart.getItemID().equals(finalMostFrequent)){
                                spareParts.add(0, sparePart);
                            } else {
                                spareParts.add(sparePart);
                            }
                            sparepartsAdapter = new SparePartsAdapter(context, spareParts);
                            sparepartsRV.setAdapter(sparepartsAdapter);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}