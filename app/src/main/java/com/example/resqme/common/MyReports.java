package com.example.resqme.common;

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
import com.example.resqme.customer.CustomerProfile;
import com.example.resqme.model.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyReports extends AppCompatActivity {

    RecyclerView myReportsRV;
    DatabaseReference myReportsDB;
    MyReportAdapter myReportAdapter;
    ArrayList<Report> reports;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        initToolbar();
        forceRTLIfSupported();
        myReportsRV = findViewById(R.id.myreports_recycler);
        context = this.getApplicationContext();
        myReportsDB = FirebaseDatabase.getInstance().getReference().child("Reports");
        myReportsRV.setHasFixedSize(true);
        myReportsRV.setLayoutManager(new LinearLayoutManager(this));
        reports = new ArrayList<>();
        myReportAdapter = new MyReportAdapter(this, reports);
        myReportsRV.setAdapter(myReportAdapter);

        myReportsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reports.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Report report = dataSnapshot.getValue(Report.class);
                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    if(report.getUserID().equals(c_userid)){
                        reports.add(report);
                        myReportAdapter = new MyReportAdapter(context, reports);
                        myReportsRV.setAdapter(myReportAdapter);
                    }
                }
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