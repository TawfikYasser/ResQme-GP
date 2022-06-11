package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Report;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MyReports extends AppCompatActivity {

    RecyclerView myReportsRV;
    DatabaseReference myReportsDB;
    MyReportAdapter myReportAdapter;
    ArrayList<Report> reports;
    Context context;
    LinearLayout noReports;
    private FirebaseAuth mAuth;
    ShimmerFrameLayout shimmerReportsLayout;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = MyReports.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        forceRTLIfSupported();
        shimmerReportsLayout = findViewById(R.id.reports_shimmer_layout);
        shimmerReportsLayout.startShimmer();
        mAuth = FirebaseAuth.getInstance();
        noReports = findViewById(R.id.no_reports_layout);
        myReportsRV = findViewById(R.id.myreports_recycler);
        context = this.getApplicationContext();
        myReportsDB = FirebaseDatabase.getInstance().getReference().child("Reports");
        myReportsRV.setHasFixedSize(true);
        myReportsRV.setLayoutManager(new LinearLayoutManager(this));
        OverScrollDecoratorHelper.setUpOverScroll(myReportsRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        reports = new ArrayList<>();
        myReportAdapter = new MyReportAdapter(this, reports);
        myReportsRV.setAdapter(myReportAdapter);

        myReportsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerReportsLayout.isShimmerStarted()){
                    shimmerReportsLayout.startShimmer();
                    shimmerReportsLayout.setVisibility(View.VISIBLE);
                    myReportsRV.setVisibility(View.GONE);
                }
                reports.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        Report report = dataSnapshot.getValue(Report.class);
                        if(report.getUserID().equals(mAuth.getCurrentUser().getUid()) && !report.getReportStatus().equals("REPLY_REPORT_PENDING")){
                            reports.add(report);
                            if(reports.size() !=0){
                                myReportAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
                shimmerReportsLayout.stopShimmer();
                shimmerReportsLayout.setVisibility(View.GONE);
                myReportsRV.setVisibility(View.VISIBLE);
                if(reports.size() == 0){
                    noReports.setVisibility(View.VISIBLE);
                }else{
                    noReports.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myReportsDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                reports.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        Report report = dataSnapshot.getValue(Report.class);
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                        if(report.getUserID().equals(c_userid) && !report.getReportStatus().equals("REPLY_REPORT_PENDING")){
                            reports.add(report);
                            if(reports.size() !=0){
                                myReportAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                if(reports.size() == 0){
                    reports.clear();
                    myReportAdapter.notifyDataSetChanged();
                }
                if(reports.size() == 0){
                    noReports.setVisibility(View.VISIBLE);
                }else{
                    noReports.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_myreports);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تقاريري");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(MyReports.this, R.style.Theme_ResQme);
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