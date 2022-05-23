package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.customer.WinchRequests;
import com.example.resqme.model.SparePartsRequest;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SP_Spare_Parts_Requests extends AppCompatActivity {
    RecyclerView spareRequestRV;
    DatabaseReference spareRequestsDB, CustomerDB;
    SPSparePartsRequestsAdapter spareRequestsAdapter;
    ArrayList<SparePartsRequest> sparePartsRequests;
    Context context, context_2;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_spare_parts_requests);
        initToolbar();
        forceRTLIfSupported();
        spareRequestRV = findViewById(R.id.sp_spare_parts_requests_recycler);
        context = this.getApplicationContext();
        context_2 = SP_Spare_Parts_Requests.this;
        view = this.getWindow().getDecorView().getRootView();
        spareRequestsDB = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
        CustomerDB = FirebaseDatabase.getInstance().getReference().child("Customer");
        spareRequestRV.setHasFixedSize(true);
        spareRequestRV.setLayoutManager(new LinearLayoutManager(this));
        sparePartsRequests = new ArrayList<>();
        spareRequestsAdapter = new SPSparePartsRequestsAdapter(sparePartsRequests, context, CustomerDB, context_2, view);
        spareRequestRV.setAdapter(spareRequestsAdapter);
        spareRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sparePartsRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePartsRequest sparePartsRequest = dataSnapshot.getValue(SparePartsRequest.class);
                    SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                    String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
                    if(sparePartsRequest.getSparePartOwnerID().equals(sp_userid)){
                        sparePartsRequests.add(sparePartsRequest);
                        spareRequestsAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sp_spare_parts_requests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات قطع الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SP_Spare_Parts_Requests.this, R.style.Theme_ResQme);
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