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
import com.example.resqme.serviceProvider.ServiceProviderAddService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){ // User is logged in, get the user type from local data and go to home.
            goToHome(currentUser);
        }else{ // User is logged out, go to login/registration.
            Intent loginIntent = new Intent(Splash.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void goToHome(FirebaseUser currentUser) {
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences userDataSP = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        if(userData.contains("C_EMAIL")){
            String c_email = userData.getString("C_EMAIL","C_DEFAULT");
            if(currentUser.getEmail().equals(c_email)){
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
            if(currentUser.getEmail().equals(sp_email)){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(Splash.this, ServiceProviderAddService.class);
                        startActivity(i);
                        finish();
                    }
                }, 1000);
            }
        }
    }
}