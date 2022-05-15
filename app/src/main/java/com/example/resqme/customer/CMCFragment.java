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
import com.example.resqme.model.Report;
import com.example.resqme.model.SparePart;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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


        cmcDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cmcs.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMC cmc = dataSnapshot.getValue(CMC.class);
                    if(cmc.getCmcStatus().equals("Approved") && cmc.getCmcAvailability().equals("Available")){
                        cmcs.add(cmc);
                        cmcAdapter = new CMCAdapter(context, cmcs);
                        cmcRV.setAdapter(cmcAdapter);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}