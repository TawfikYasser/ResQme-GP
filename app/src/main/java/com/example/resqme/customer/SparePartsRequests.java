package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.resqme.R;
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.SparePartsRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class SparePartsRequests extends AppCompatActivity {
    RecyclerView sparePartsRequestRV;
    DatabaseReference sparePartsRequestsDB, serviceProvidersDB, sparePartsItemDB;
    SparePartsRequestAdapter sparePartsRequestsAdapter;
    ArrayList<SparePartsRequest> sparePartsRequests;
    Context context, context_2;
    View view;
    LinearLayout noRequest;
    ShimmerFrameLayout shimmerCustomerSparePartsRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_parts_requests);
        initToolbar();
        forceRTLIfSupported();
        shimmerCustomerSparePartsRequests = findViewById(R.id.spareparts_requests_customer_shimmer);
        shimmerCustomerSparePartsRequests.startShimmer();
        noRequest = findViewById(R.id.no_request_layout_spare_parts);
        sparePartsRequestRV = findViewById(R.id.spareparts_requests_recycler);
        context = this.getApplicationContext();
        context_2 = SparePartsRequests.this;
        view = this.getWindow().getDecorView().getRootView();
        sparePartsRequestsDB = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        sparePartsItemDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        sparePartsRequestRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        sparePartsRequestRV.setLayoutManager(linearLayoutManager);
        OverScrollDecoratorHelper.setUpOverScroll(sparePartsRequestRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        sparePartsRequests = new ArrayList<>();
        sparePartsRequestsAdapter = new SparePartsRequestAdapter(this, sparePartsRequests, serviceProvidersDB, sparePartsItemDB, context_2, view);
        sparePartsRequestRV.setAdapter(sparePartsRequestsAdapter);


        sparePartsRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerCustomerSparePartsRequests.isShimmerStarted()){
                    shimmerCustomerSparePartsRequests.startShimmer();
                    shimmerCustomerSparePartsRequests.setVisibility(View.VISIBLE);
                    sparePartsRequestRV.setVisibility(View.GONE);
                }
                sparePartsRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePartsRequest sparePartsRequest = dataSnapshot.getValue(SparePartsRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(sparePartsRequest.getCustomerID().equals(c_userid)){
                        sparePartsRequests.add(sparePartsRequest);
                        sparePartsRequestsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerCustomerSparePartsRequests.stopShimmer();
                shimmerCustomerSparePartsRequests.setVisibility(View.GONE);
                sparePartsRequestRV.setVisibility(View.VISIBLE);
                if(sparePartsRequests.size() == 0){
                    noRequest.setVisibility(View.VISIBLE);
                }else{
                    noRequest.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_spareparts_requests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات قطع الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SparePartsRequests.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}