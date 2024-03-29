package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.SparePart;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class ServiceProviderHome_SpareParts extends AppCompatActivity {
    // Spare Parts Data
    RecyclerView sparePartsSPHomeRV;
    DatabaseReference sparePartsDB;
    SparePartsSPHomeAdapter SparePartsAdapter;
    ArrayList<SparePart> spareParts;
    ShimmerFrameLayout shimmerFrameLayoutSpareSP;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home_spare_parts);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = ServiceProviderHome_SpareParts.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        forceRTLIfSupported();
        shimmerFrameLayoutSpareSP = findViewById(R.id.spare_parts_item_sp_shimmer_layout);
        shimmerFrameLayoutSpareSP.startShimmer();
        sparePartsSPHomeRV = findViewById(R.id.spare_parts_recycler_sp_home);
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_email = userData.getString("SP_EMAIL","SP_DEFAULT");
        String sp_username = userData.getString("SP_USERNAME","SP_DEFAULT");
        String sp_password = userData.getString("SP_PASSWORD","SP_DEFAULT");
        String sp_address = userData.getString("SP_ADDRESS","SP_DEFAULT");
        String sp_whatsapp = userData.getString("SP_WHATSAPP","SP_DEFAULT");
        String sp_dob = userData.getString("SP_DOB","SP_DEFAULT");
        String sp_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        String sp_usertype = userData.getString("SP_USERTYPE","SP_DEFAULT");
        String sp_usergender = userData.getString("SP_USERGENDER","SP_DEFAULT");
        String sp_userrate = userData.getString("SP_USERRATE","SP_DEFAULT");
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        String sp_serviceType = userData.getString("SP_ServiceType","SP_DEFAULT");
        String sp_cmc = userData.getString("SP_CMC","SP_DEFAULT");
        String sp_spareParts = userData.getString("SP_SPARE_PARTS","SP_DEFAULT");
        String sp_winch = userData.getString("SP_WINCH","SP_DEFAULT");

        // Will be moved to the activity
        sparePartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        sparePartsSPHomeRV.setHasFixedSize(true);
        sparePartsSPHomeRV.setLayoutManager(new LinearLayoutManager(this));
        OverScrollDecoratorHelper.setUpOverScroll(sparePartsSPHomeRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        spareParts = new ArrayList<>();
        SparePartsAdapter = new SparePartsSPHomeAdapter(ServiceProviderHome_SpareParts.this, spareParts);
        sparePartsSPHomeRV.setAdapter(SparePartsAdapter);
        sparePartsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerFrameLayoutSpareSP.isShimmerStarted()){
                    shimmerFrameLayoutSpareSP.startShimmer();
                    shimmerFrameLayoutSpareSP.setVisibility(View.VISIBLE);
                    sparePartsSPHomeRV.setVisibility(View.GONE);
                }
                spareParts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePart sparePart = dataSnapshot.getValue(SparePart.class);
                    if(sparePart.getItemServiceProviderId().equals(sp_userid)){
                        spareParts.add(sparePart);
                        SparePartsAdapter.notifyDataSetChanged();
                    }
                }
                shimmerFrameLayoutSpareSP.stopShimmer();
                shimmerFrameLayoutSpareSP.setVisibility(View.GONE);
                sparePartsSPHomeRV.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_SPHOMESPARE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("بيانات قطع الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServiceProviderHome_SpareParts.this, R.style.Theme_ResQme);
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