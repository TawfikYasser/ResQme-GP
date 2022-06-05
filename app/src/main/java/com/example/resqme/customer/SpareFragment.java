package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.LogDataModel;
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

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


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
    String Filter;
    Button Search;
    ChipGroup FilterGp;
    DatabaseReference sparepartsDB;
    SparePartsAdapter sparepartsAdapter;
    ArrayList<SparePart> spareParts;
    Context context, context_2;
    ShimmerFrameLayout shimmerFrameLayoutSpareCustomer;
    ArrayList<String> sparePartsIDs;
    DatabaseReference logDB;
    FloatingActionButton FilterBtn;
    LinearLayout noFilterResult;
    SearchView spareSearchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spare, container, false);
        shimmerFrameLayoutSpareCustomer = view.findViewById(R.id.spare_customer_shimmer);
        shimmerFrameLayoutSpareCustomer.startShimmer();
        FilterBtn = view.findViewById(R.id.FilterButtonSpareParts);
        noFilterResult = view.findViewById(R.id.no_request_layout_spare_fragment);
        spareSearchView = view.findViewById(R.id.search_spare_parts);
        spareSearchView.clearFocus();
        sparePartsIDs = new ArrayList<>();
        sparepartsRV = view.findViewById(R.id.spare_parts_recycler);
        context = getActivity().getApplicationContext();
        context_2 = SpareFragment.this.getContext();
        sparepartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        logDB = FirebaseDatabase.getInstance().getReference().child("LOG");
        sparepartsRV.setHasFixedSize(true);
        sparepartsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        OverScrollDecoratorHelper.setUpOverScroll(sparepartsRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        spareParts = new ArrayList<>();
        sparepartsAdapter = new SparePartsAdapter(getActivity(), spareParts, context_2, view);
        sparepartsRV.setAdapter(sparepartsAdapter);

        FilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog FilterSheet = new BottomSheetDialog(context_2, R.style.BottomSheetDialogTheme);
                FilterSheet.setContentView(R.layout.filter_spareparts_layout);
                FilterSheet.setCanceledOnTouchOutside(true);
                FilterGp = FilterSheet.findViewById(R.id.chipGroupSP);
                Search = FilterSheet.findViewById(R.id.Get_Search_SP_Result);
                DatabaseReference DBFilter = FirebaseDatabase.getInstance().getReference().child("SpareParts");
                Search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FilterSheet.cancel();
                        for(int i=0;i< FilterGp.getChildCount() ;i++){
                            Chip chip= (Chip) FilterGp.getChildAt(i);
                            if(chip.isChecked()){
                                Filter = (String) chip.getText();
                                spareParts.clear();
                                DBFilter.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!shimmerFrameLayoutSpareCustomer.isShimmerStarted()){
                                            shimmerFrameLayoutSpareCustomer.startShimmer();
                                            shimmerFrameLayoutSpareCustomer.setVisibility(View.VISIBLE);
                                            sparepartsRV.setVisibility(View.GONE);
                                        }
                                        spareParts.clear();
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                                            if(sparePart.getItemStatus().equals("Approved") && sparePart.getItemAvailability().equals("Available") && sparePart.getItemCarType().equals(Filter)){
                                                spareParts.add(sparePart);
                                                sparepartsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        if(spareParts.size() == 0){
                                            noFilterResult.setVisibility(View.VISIBLE);
                                        }else{
                                            noFilterResult.setVisibility(View.GONE);
                                        }
                                        shimmerFrameLayoutSpareCustomer.stopShimmer();
                                        shimmerFrameLayoutSpareCustomer.setVisibility(View.GONE);
                                        sparepartsRV.setVisibility(View.VISIBLE);
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
                sparePartsIDs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LogDataModel logData = dataSnapshot.getValue(LogDataModel.class);
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        if(logData.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && logData.getEventType().equals("SERVICE_CLICK") && logData.getServiceName().equals("SPARE_PARTS")){
                            sparePartsIDs.add(logData.getServiceID());
                        }
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

                String finalMostFrequent = mostFrequent;
                sparepartsDB.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!shimmerFrameLayoutSpareCustomer.isShimmerStarted()){
                            shimmerFrameLayoutSpareCustomer.startShimmer();
                            shimmerFrameLayoutSpareCustomer.setVisibility(View.VISIBLE);
                            sparepartsRV.setVisibility(View.GONE);
                        }
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
                                sparepartsAdapter.notifyDataSetChanged();
                            }
                        }
                        shimmerFrameLayoutSpareCustomer.stopShimmer();
                        shimmerFrameLayoutSpareCustomer.setVisibility(View.GONE);
                        sparepartsRV.setVisibility(View.VISIBLE);
                        if(spareParts.size() == 0){
                            noFilterResult.setVisibility(View.VISIBLE);
                        }else{
                            noFilterResult.setVisibility(View.GONE);
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

        spareSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchSpareParts(query);
                return true;
            }
        });
        return view;
    }

    private void searchSpareParts(String query) {
        sparepartsDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerFrameLayoutSpareCustomer.isShimmerStarted()){
                    shimmerFrameLayoutSpareCustomer.startShimmer();
                    shimmerFrameLayoutSpareCustomer.setVisibility(View.VISIBLE);
                    sparepartsRV.setVisibility(View.GONE);
                }
                spareParts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                    if(sparePart.getItemStatus().equals("Approved") && sparePart.getItemAvailability().equals("Available")
                    && sparePart.getItemName().toLowerCase().contains(query.toLowerCase())){
                        spareParts.add(sparePart);
                        sparepartsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerFrameLayoutSpareCustomer.stopShimmer();
                shimmerFrameLayoutSpareCustomer.setVisibility(View.GONE);
                sparepartsRV.setVisibility(View.VISIBLE);
                if(spareParts.size() == 0){
                    noFilterResult.setVisibility(View.VISIBLE);
                }else{
                    noFilterResult.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
