package com.example.resqme.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CMCFragment extends Fragment {


    public CMCFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    //InitViews
    RecyclerView cmcRV;
    DatabaseReference cmcDB;
    CMCAdapter cmcAdapter;
    ArrayList<CMC> cmcs;
    Context context;

    ShimmerFrameLayout shimmerFrameLayoutCMCCustomer;
    ArrayList<String> cmcIDs;
    DatabaseReference logDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_m_c, container, false);
        shimmerFrameLayoutCMCCustomer = view.findViewById(R.id.cmc_customer_shimmer);
        shimmerFrameLayoutCMCCustomer.startShimmer();
        cmcRV = view.findViewById(R.id.cmc_recycler);
        context = getActivity().getApplicationContext();
        cmcDB = FirebaseDatabase.getInstance().getReference().child("CMCs");
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        cmcIDs = new ArrayList<>();
        cmcRV.setHasFixedSize(true);
        cmcRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        cmcs = new ArrayList<>();
        cmcAdapter = new CMCAdapter(getActivity(), cmcs);
        cmcRV.setAdapter(cmcAdapter);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutCMCCustomer.stopShimmer();
                shimmerFrameLayoutCMCCustomer.setVisibility(View.GONE);
                cmcRV.setVisibility(View.VISIBLE);
            }  //end of run
        }, 2000);


        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //All logic will be here
                cmcIDs.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){

                        if(logData.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && logData.getIsService().equals("TRUE") && !logData.getClickedServiceID().isEmpty()){
                            cmcIDs.add(logData.getClickedServiceID());
                        }

                    }
                }
                // Count the occurrences of each clicked service ID and get the most frequent one
                HashMap<String, Integer> map = new HashMap<>();
                for (String s : cmcIDs) {
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
                String finalMostFrequent = mostFrequent;
                cmcDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cmcs.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            CMC cmc = dataSnapshot.getValue(CMC.class);
                            if(cmc.getCmcStatus().equals("Approved") && cmc.getCmcAvailability().equals("Available")){
                                // Put the spare part with the most frequent ID in the first position
                                if(cmc.getCmcID().equals(finalMostFrequent)){
                                    cmcs.add(0, cmc);
                                } else {
                                    cmcs.add(cmc);
                                }
                                cmcAdapter = new CMCAdapter(context, cmcs);
                                cmcRV.setAdapter(cmcAdapter);
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


//        cmcDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                cmcs.clear();
//                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    CMC cmc = dataSnapshot.getValue(CMC.class);
//                    if(cmc.getCmcStatus().equals("Approved") && cmc.getCmcAvailability().equals("Available")){
//                        cmcs.add(cmc);
//                        cmcAdapter = new CMCAdapter(context, cmcs);
//                        cmcRV.setAdapter(cmcAdapter);
//                    }
//
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return view;
    }
}