package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.example.resqme.model.Customer;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHome extends AppCompatActivity {
    Button button;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logouthomec);
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
        Intent loginIntent = new Intent(CustomerHome.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}