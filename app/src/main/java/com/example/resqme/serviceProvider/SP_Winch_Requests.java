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
import com.example.resqme.customer.WinchRequestsAdapter;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SP_Winch_Requests extends AppCompatActivity {
    RecyclerView winchRequestRV;
    DatabaseReference winchRequestsDB, CustomerDB;
    SPWinchRequestsAdapter winchRequestsAdapter;
    ArrayList<WinchRequest> winchRequests;
    Context context, context_2;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_winch_requests);

        initToolbar();
        forceRTLIfSupported();

        winchRequestRV = findViewById(R.id.sp_winch_requests_recycler);
        context = this.getApplicationContext();
        context_2 = SP_Winch_Requests.this;
        view = this.getWindow().getDecorView().getRootView();
        winchRequestsDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
        CustomerDB = FirebaseDatabase.getInstance().getReference().child("Customer");
        winchRequestRV.setHasFixedSize(true);
        winchRequestRV.setLayoutManager(new LinearLayoutManager(this));
        winchRequests = new ArrayList<>();
        winchRequestsAdapter = new SPWinchRequestsAdapter(winchRequests, this, CustomerDB, context_2, view);
        winchRequestRV.setAdapter(winchRequestsAdapter);
        winchRequestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                winchRequests.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                    SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                    String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
                    if(winchRequest.getWinchOwnerID().equals(sp_userid)){
                        winchRequests.add(winchRequest);
                        winchRequestsAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sp_winch_requests);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("طلبات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SP_Winch_Requests.this, R.style.Theme_ResQme);
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