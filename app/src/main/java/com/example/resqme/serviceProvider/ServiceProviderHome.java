package com.example.resqme.serviceProvider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.example.resqme.customer.CustomerHome;
import com.google.firebase.auth.FirebaseAuth;

public class ServiceProviderHome extends AppCompatActivity {
    Button button;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);


        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logouthomesp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendToLogin();
                finish();
            }
        });
    }

    void sendToLogin() {
        Intent loginIntent = new Intent(ServiceProviderHome.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}