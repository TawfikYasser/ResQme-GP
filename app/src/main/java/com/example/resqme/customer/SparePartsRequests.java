package com.example.resqme.customer;

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
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.SparePartsRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SparePartsRequests extends AppCompatActivity {
    RecyclerView sparePartsRequestRV;
    DatabaseReference sparePartsRequestsDB, serviceProvidersDB, sparePartsItemDB;
    SparePartsRequestAdapter sparePartsRequestsAdapter;
    ArrayList<SparePartsRequest> sparePartsRequests;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_parts_requests);
        initToolbar();
        forceRTLIfSupported();

        sparePartsRequestRV = findViewById(R.id.spareparts_requests_recycler);
        context = this.getApplicationContext();
        sparePartsRequestsDB = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        sparePartsItemDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        sparePartsRequestRV.setHasFixedSize(true);
        sparePartsRequestRV.setLayoutManager(new LinearLayoutManager(this));
        sparePartsRequests = new ArrayList<>();
        sparePartsRequestsAdapter = new SparePartsRequestAdapter(this, sparePartsRequests, serviceProvidersDB, sparePartsItemDB);
        sparePartsRequestRV.setAdapter(sparePartsRequestsAdapter);


        sparePartsRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sparePartsRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePartsRequest sparePartsRequest = dataSnapshot.getValue(SparePartsRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(sparePartsRequest.getCustomerID().equals(c_userid)){
                        sparePartsRequests.add(sparePartsRequest);
                        sparePartsRequestsAdapter = new SparePartsRequestAdapter(context, sparePartsRequests, serviceProvidersDB, sparePartsItemDB);
                        sparePartsRequestRV.setAdapter(sparePartsRequestsAdapter);
                    }
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