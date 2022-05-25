package com.example.resqme.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.model.CMC;
import com.example.resqme.model.LogDataModel;
import com.example.resqme.model.Report;
import com.example.resqme.model.SparePart;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    String Filter;
    RecyclerView cmcRV;
    FloatingActionButton FilterBtn;
    DatabaseReference cmcDB;
    CMCAdapter cmcAdapter;
    ArrayList<CMC> cmcs;
    Context context,context_2;
    Button Search;
    ChipGroup FilterGp;
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
        FilterBtn = view.findViewById(R.id.FilterButtonCMC);
        cmcRV = view.findViewById(R.id.cmc_recycler);
        context = getActivity().getApplicationContext();
        context_2 = CMCFragment.this.getContext();
        cmcDB = FirebaseDatabase.getInstance().getReference().child("CMCs");
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        cmcIDs = new ArrayList<>();
        cmcRV.setHasFixedSize(true);
        cmcRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        cmcs = new ArrayList<>();
        cmcAdapter = new CMCAdapter(getActivity(), cmcs);
        cmcRV.setAdapter(cmcAdapter);
        FilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog FilterSheet = new BottomSheetDialog(context_2);
                FilterSheet.setContentView(R.layout.filter_cmc_layout);
                FilterSheet.setCanceledOnTouchOutside(true);
                FilterGp = FilterSheet.findViewById(R.id.chipGroupcmc);
                Search = FilterSheet.findViewById(R.id.Get_Search_CMC_Result);
                DatabaseReference DBFilter = FirebaseDatabase.getInstance().getReference("CMCs");
                Search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i=0;i<FilterGp.getChildCount() ;i++){
                            Chip chip= (Chip) FilterGp.getChildAt(i);
                            if(chip.isChecked()){
                                Filter = (String) chip.getText();
                                DBFilter.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!shimmerFrameLayoutCMCCustomer.isShimmerStarted()){
                                            shimmerFrameLayoutCMCCustomer.startShimmer();
                                            shimmerFrameLayoutCMCCustomer.setVisibility(View.VISIBLE);
                                            cmcRV.setVisibility(View.GONE);
                                        }
                                        cmcs.clear();
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            CMC cmc = dataSnapshot.getValue(CMC.class);
                                            if(cmc.getCmcStatus().equals("Approved") && cmc.getCmcAvailability().equals("Available") && cmc.getCmcBrand().equals(Filter)){
                                                cmcs.add(cmc);
                                                cmcAdapter = new CMCAdapter(context, cmcs);
                                                cmcAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        shimmerFrameLayoutCMCCustomer.stopShimmer();
                                        shimmerFrameLayoutCMCCustomer.setVisibility(View.GONE);
                                        cmcRV.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                });
                FilterSheet.show();
            }
        });

        logDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //All logic will be here
                cmcIDs.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        if(logData.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && logData.getEventType().equals("SERVICE_CLICK") && logData.getServiceName().equals("CMC")){
                                cmcIDs.add(logData.getServiceID());
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
                        if(!shimmerFrameLayoutCMCCustomer.isShimmerStarted()){
                            shimmerFrameLayoutCMCCustomer.startShimmer();
                            shimmerFrameLayoutCMCCustomer.setVisibility(View.VISIBLE);
                            cmcRV.setVisibility(View.GONE);
                        }
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
                                cmcAdapter.notifyDataSetChanged();
                            }
                        }
                        shimmerFrameLayoutCMCCustomer.stopShimmer();
                        shimmerFrameLayoutCMCCustomer.setVisibility(View.GONE);
                        cmcRV.setVisibility(View.VISIBLE);
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