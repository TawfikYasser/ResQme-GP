package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.common.MyReports;
import com.example.resqme.model.Report;
import com.example.resqme.model.WinchRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class WinchRequests extends AppCompatActivity {

    RecyclerView winchRequestRV;
    DatabaseReference winchRequestsDB, serviceProvidersDB;
    WinchRequestsAdapter winchRequestsAdapter;
    ArrayList<WinchRequest> winchRequests;
    Context context, context_2;
    View view;
    LinearLayout noRequests;
    ShimmerFrameLayout shimmerCustomerWinchRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winch_requests);
        initToolbar();
        forceRTLIfSupported();
        shimmerCustomerWinchRequests = findViewById(R.id.winch_requests_customer_shimmer);
        shimmerCustomerWinchRequests.startShimmer();
        winchRequestRV = findViewById(R.id.winch_requests_recycler);
        noRequests = findViewById(R.id.no_request_layout);
        context = this.getApplicationContext();
        context_2 = WinchRequests.this;
        view = this.getWindow().getDecorView().getRootView();
        winchRequestsDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        winchRequestRV.setLayoutManager(linearLayoutManager);
        winchRequestRV.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(winchRequestRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        winchRequests = new ArrayList<>();
        winchRequestsAdapter = new WinchRequestsAdapter(this, winchRequests, serviceProvidersDB, view, context_2);
        winchRequestRV.setAdapter(winchRequestsAdapter);
        winchRequestsDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerCustomerWinchRequests.isShimmerStarted()){
                    shimmerCustomerWinchRequests.startShimmer();
                    shimmerCustomerWinchRequests.setVisibility(View.VISIBLE);
                    winchRequestRV.setVisibility(View.GONE);
                }
                winchRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(winchRequest.getCustomerID().equals(c_userid)){
                        winchRequests.add(winchRequest);
                        winchRequestsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerCustomerWinchRequests.stopShimmer();
                shimmerCustomerWinchRequests.setVisibility(View.GONE);
                winchRequestRV.setVisibility(View.VISIBLE);
                if(winchRequests.size() == 0){
                    noRequests.setVisibility(View.VISIBLE);
                }else{
                    noRequests.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_winchrequests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(WinchRequests.this, R.style.Theme_ResQme);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 35){



        }
    }
}