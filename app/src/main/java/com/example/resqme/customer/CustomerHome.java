package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.example.resqme.model.Customer;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHome extends AppCompatActivity {
    Button button;
    private FirebaseAuth mAuth;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logouthomec);

        textView = findViewById(R.id.cdata_home);


        SharedPreferences userData = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_email = userData.getString("C_EMAIL","C_DEFAULT");
        String c_username = userData.getString("C_USERNAME","C_DEFAULT");
        String c_password = userData.getString("C_PASSWORD","C_DEFAULT");
        String c_address = userData.getString("C_ADDRESS","C_DEFAULT");
        String c_whatsapp = userData.getString("C_WHATSAPP","C_DEFAULT");
        String c_dob = userData.getString("C_DOB","C_DEFAULT");
        String c_userimage = userData.getString("C_USERIMAGE","C_DEFAULT");
        String c_usertype = userData.getString("C_USERTYPE","C_DEFAULT");
        String c_usergender = userData.getString("C_USERGENDER","C_DEFAULT");
        String c_carid = userData.getString("C_CARID","C_DEFAULT");
        String c_userrate = userData.getString("C_USERRATE","C_DEFAULT");
        String c_userid = userData.getString("C_USERID","C_DEFAULT");

        textView.setText(c_email + " - "+ c_usertype + " - "+ c_userid);


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