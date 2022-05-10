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
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.model.RequestDetailsModel;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CarHistory extends AppCompatActivity {
    RecyclerView requestDetailsRV;
    DatabaseReference requestDetailsDB,winchRequestsDB;
    RequestDetailsAdapter requestDetailsAdapter;
    ArrayList<RequestDetailsModel> requestDetails;
    Context context, context_2;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_history);
        initToolbar();
        forceRTLIfSupported();
        requestDetailsRV = findViewById(R.id.request_details_recycler);
        context = this.getApplicationContext();
        context_2 = CarHistory.this;
        view = this.getWindow().getDecorView().getRootView();
        requestDetailsDB = FirebaseDatabase.getInstance().getReference().child("RequestDetails");
        winchRequestsDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
        requestDetailsRV.setHasFixedSize(true);
        requestDetailsRV.setLayoutManager(new LinearLayoutManager(this));
        requestDetails = new ArrayList<>();
        requestDetailsAdapter = new RequestDetailsAdapter(this, requestDetails, winchRequestsDB, view, context_2);
        requestDetailsRV.setAdapter(requestDetailsAdapter);
        requestDetailsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestDetails.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    RequestDetailsModel requestDetailsModel = dataSnapshot.getValue(RequestDetailsModel.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(requestDetailsModel.getCustomerId().equals(c_userid)){
                        requestDetails.add(requestDetailsModel);
                        requestDetailsAdapter = new RequestDetailsAdapter(context, requestDetails,winchRequestsDB , view, context_2);
                        requestDetailsRV.setAdapter(requestDetailsAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_requestdetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CarHistory.this, R.style.Theme_ResQme);
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