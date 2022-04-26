package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.common.MyReports;
import com.example.resqme.model.Report;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WinchRequests extends AppCompatActivity {

    RecyclerView winchRequestRV;
    DatabaseReference winchRequestsDB, serviceProvidersDB;
    WinchRequestsAdapter winchRequestsAdapter;
    ArrayList<WinchRequest> winchRequests;
    Context context, context_2;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winch_requests);
        initToolbar();
        forceRTLIfSupported();
        winchRequestRV = findViewById(R.id.winch_requests_recycler);
        context = this.getApplicationContext();
        context_2 = WinchRequests.this;
        view = this.getWindow().getDecorView().getRootView();
        winchRequestsDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        winchRequestRV.setHasFixedSize(true);
        winchRequestRV.setLayoutManager(new LinearLayoutManager(this));
        winchRequests = new ArrayList<>();
        winchRequestsAdapter = new WinchRequestsAdapter(this, winchRequests, serviceProvidersDB, view, context_2);
        winchRequestRV.setAdapter(winchRequestsAdapter);
        winchRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                winchRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(winchRequest.getCustomerID().equals(c_userid)){
                        winchRequests.add(winchRequest);
                        winchRequestsAdapter = new WinchRequestsAdapter(context, winchRequests, serviceProvidersDB, view, context_2);
                        winchRequestRV.setAdapter(winchRequestsAdapter);
                    }
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