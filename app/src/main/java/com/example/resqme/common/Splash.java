package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
            // We need to check if the user is customer or service provider
            getUser();
        }else{
            // user logged out
            Intent loginIntent = new Intent(Splash.this, Login.class);
            startActivity(loginIntent);
            finish();
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

        // If the usertype is 'عميل'
        // Go to Customer Home

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