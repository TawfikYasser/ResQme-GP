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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.resqme.R;
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.WinchRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CMCRequests extends AppCompatActivity {

    RecyclerView cmcRequestRV;
    DatabaseReference cmcRequestsDB, serviceProvidersDB;
    CMCRequestsAdapter cmcRequestsAdapter;
    ArrayList<CMCRequest> cmcRequests;
    Context context, context_2;
    View view;
    LinearLayout noRequests;
    ShimmerFrameLayout shimmerCustomerCMCRequests;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmcrequests);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = CMCRequests.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        forceRTLIfSupported();
        shimmerCustomerCMCRequests = findViewById(R.id.cmc_requests_customer_shimmer);
        shimmerCustomerCMCRequests.startShimmer();
        noRequests = findViewById(R.id.no_request_layout_cmc);
        cmcRequestRV = findViewById(R.id.cmc_requests_recycler);
        context = this.getApplicationContext();
        context_2 = CMCRequests.this;
        view = this.getWindow().getDecorView().getRootView();
        cmcRequestsDB = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
        serviceProvidersDB = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        cmcRequestRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cmcRequestRV.setLayoutManager(linearLayoutManager);
        OverScrollDecoratorHelper.setUpOverScroll(cmcRequestRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        cmcRequests = new ArrayList<>();
        cmcRequestsAdapter = new CMCRequestsAdapter(this, cmcRequests, serviceProvidersDB, context_2, view);
        cmcRequestRV.setAdapter(cmcRequestsAdapter);

        cmcRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerCustomerCMCRequests.isShimmerStarted()){
                    shimmerCustomerCMCRequests.startShimmer();
                    shimmerCustomerCMCRequests.setVisibility(View.VISIBLE);
                    cmcRequestRV.setVisibility(View.GONE);
                }
                cmcRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CMCRequest cmcRequest = dataSnapshot.getValue(CMCRequest.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(cmcRequest.getCustomerID().equals(c_userid)){
                        cmcRequests.add(cmcRequest);
                        cmcRequestsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerCustomerCMCRequests.stopShimmer();
                shimmerCustomerCMCRequests.setVisibility(View.GONE);
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