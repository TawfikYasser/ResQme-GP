package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.common.MyReports;
import com.example.resqme.model.Report;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class WinchRequests extends AppCompatActivity {

    RecyclerView winchRequestRV;
    DatabaseReference winchRequestsDB;
    WinchRequestsAdapter winchRequestsAdapter;
    ArrayList<WinchRequest> winchRequests;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winch_requests);
        initToolbar();
        forceRTLIfSupported();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_myreports);
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

}