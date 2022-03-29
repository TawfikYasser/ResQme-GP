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
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CMCRequests extends AppCompatActivity {

    RecyclerView cmcRequestRV;
    DatabaseReference cmcRequestsDB, serviceProvidersDB;
    CMCRequestsAdapter cmcRequestsAdapter;
    ArrayList<CMCRequest> cmcRequests;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmcrequests);
        initToolbar();
        forceRTLIfSupported();

        cmcRequestRV = findViewById(R.id.cmc_requests_recycler);
        context = this.getApplicationContext();
        cmcRequestsDB = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        cmcRequestRV.setHasFixedSize(true);
        cmcRequestRV.setLayoutManager(new LinearLayoutManager(this));
        cmcRequests = new ArrayList<>();
        cmcRequestsAdapter = new CMCRequestsAdapter(this, cmcRequests, serviceProvidersDB);
        cmcRequestRV.setAdapter(cmcRequestsAdapter);

        cmcRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cmcRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMCRequest cmcRequest = dataSnapshot.getValue(CMCRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(cmcRequest.getCustomerID().equals(c_userid)){
                        cmcRequests.add(cmcRequest);
                        cmcRequestsAdapter = new CMCRequestsAdapter(context, cmcRequests, serviceProvidersDB);
                        cmcRequestRV.setAdapter(cmcRequestsAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_cmcrequests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات مركز الصيانة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CMCRequests.this, R.style.Theme_ResQme);
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