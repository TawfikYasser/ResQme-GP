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
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class SP_CMC_Requests extends AppCompatActivity {
    RecyclerView cmcRequestRV;
    DatabaseReference cmcRequestsDB, CustomerDB;
    SPCMCRequestsAdapter cmcRequestsAdapter;
    ArrayList<CMCRequest> cmcRequests;
    Context context, context_2;
    View view;
    LinearLayout noRequests;
    ShimmerFrameLayout shimmerSPCMCRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_cmc_requests);
        initToolbar();
        forceRTLIfSupported();
        shimmerSPCMCRequests = findViewById(R.id.cmc_requests_sp_shimmer);
        shimmerSPCMCRequests.startShimmer();
        noRequests = findViewById(R.id.no_request_layout_sp_cmc);
        cmcRequestRV = findViewById(R.id.sp_cmc_requests_recycler);
        context = this.getApplicationContext();
        context_2 = SP_CMC_Requests.this;
        view = this.getWindow().getDecorView().getRootView();
        cmcRequestsDB = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
        CustomerDB = FirebaseDatabase.getInstance().getReference().child("Customer");
        cmcRequestRV.setHasFixedSize(true);
        cmcRequestRV.setLayoutManager(new LinearLayoutManager(this));
        OverScrollDecoratorHelper.setUpOverScroll(cmcRequestRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        cmcRequests = new ArrayList<>();
        cmcRequestsAdapter = new SPCMCRequestsAdapter(cmcRequests, context, context_2, CustomerDB, view);
        cmcRequestRV.setAdapter(cmcRequestsAdapter);
        cmcRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerSPCMCRequests.isShimmerStarted()){
                    shimmerSPCMCRequests.startShimmer();
                    shimmerSPCMCRequests.setVisibility(View.VISIBLE);
                    cmcRequestRV.setVisibility(View.GONE);
                }
                cmcRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMCRequest cmcRequest = dataSnapshot.getValue(CMCRequest.class);
                    SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                    String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
                    if(cmcRequest.getCmcOwnerID().equals(sp_userid)){
                        cmcRequests.add(cmcRequest);
                        cmcRequestsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerSPCMCRequests.stopShimmer();
                shimmerSPCMCRequests.setVisibility(View.GONE);
                cmcRequestRV.setVisibility(View.VISIBLE);
                if(cmcRequests.size() == 0){
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
        Toolbar toolbar = findViewById(R.id.toolbar_sp_cmc_requests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات مراكز الصيانة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SP_CMC_Requests.this, R.style.Theme_ResQme);
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