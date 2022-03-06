package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.serviceProvider.ServiceProviderHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseCustomers;
    private DatabaseReference databaseServiceProviders;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseCustomers = FirebaseDatabase.getInstance().getReference("Customer");
        databaseServiceProviders = FirebaseDatabase.getInstance().getReference("ServiceProvider");

        if(user!=null){
            //user signed in
            SharedPreferences c_data = getSharedPreferences("CUSTOMER_LOCAL_DATA", MODE_PRIVATE);
            SharedPreferences sp_data = getSharedPreferences("SP_LOCAL_DATA", MODE_PRIVATE);
            if(c_data.contains("C_EMAIL") || sp_data.contains("SP_EMAIL")){
                checkUserData(user.getEmail());
            }else{
                getUser();
            }
        }else{
            // user logged out
            Intent loginIntent = new Intent(Splash.this, Login.class);
            startActivity(loginIntent);
            finish();
        }

    }


    void checkUserData(String email){
        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences userDataSP = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        if(userData.contains("C_EMAIL")){
            String c_email = userData.getString("C_EMAIL","C_DEFAULT");
            if(email.equals(c_email)){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Splash.this, CustomerHome.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
        }
        if(userDataSP.contains("SP_EMAIL")){
            String sp_email = userDataSP.getString("SP_EMAIL","SP_DEFAULT");
            if(email.equals(sp_email)){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Splash.this, ServiceProviderHome.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
        }
    }


    private void getUser() {
        databaseCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);

                    if (customer != null) {
                        if (customer.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                            userType = customer.getUserType();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseServiceProviders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServiceProvider serviceProvider = snapshot.getValue(ServiceProvider.class);

                    if (serviceProvider != null) {
                        if (serviceProvider.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                            userType = serviceProvider.getUserType();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(userType.equals("عميل")){
            goToCustomerHome();
        }else{
            goToSPHome();
        }
    }



    private void goToCustomerHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, CustomerHome.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }
    private void goToSPHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, ServiceProviderHome.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }
}