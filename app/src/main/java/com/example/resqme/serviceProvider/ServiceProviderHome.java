package com.example.resqme.serviceProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.google.firebase.auth.FirebaseAuth;

public class ServiceProviderHome extends AppCompatActivity  {
    LinearLayout cmc_HOME;
    LinearLayout sp_HOME;
    LinearLayout winch_HOME;
    Context context;
    String Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        context = getApplicationContext();
        initviews();
        SharedPreferences userData = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        String sp_email = userData.getString("SP_EMAIL","SP_DEFAULT");
        String sp_username = userData.getString("SP_USERNAME","SP_DEFAULT");
        String sp_password = userData.getString("SP_PASSWORD","SP_DEFAULT");
        String sp_address = userData.getString("SP_ADDRESS","SP_DEFAULT");
        String sp_whatsapp = userData.getString("SP_WHATSAPP","SP_DEFAULT");
        String sp_dob = userData.getString("SP_DOB","SP_DEFAULT");
        String sp_userimage = userData.getString("SP_USERIMAGE","SP_DEFAULT");
        String sp_usertype = userData.getString("SP_USERTYPE","SP_DEFAULT");
        String sp_usergender = userData.getString("SP_USERGENDER","SP_DEFAULT");
        String sp_userrate = userData.getString("SP_USERRATE","SP_DEFAULT");
        String sp_userid = userData.getString("SP_USERID","SP_DEFAULT");
        String sp_serviceType = userData.getString("SP_ServiceType","SP_DEFAULT");
        String sp_cmc = userData.getString("SP_CMC","SP_DEFAULT");
        String sp_spareParts = userData.getString("SP_SPARE_PARTS","SP_DEFAULT");
        String sp_winch = userData.getString("SP_WINCH","SP_DEFAULT");

        if(sp_serviceType.equalsIgnoreCase("Winch")){
            winch_HOME.setVisibility(View.VISIBLE);
        }
        else if(sp_serviceType.equalsIgnoreCase("SpareParts")){
            sp_HOME.setVisibility(View.VISIBLE);
        }
        else{
            cmc_HOME.setVisibility(View.VISIBLE);
            if(sp_spareParts.equalsIgnoreCase("TRUE")){
                sp_HOME.setVisibility(View.VISIBLE);
            }
            else if(sp_winch.equalsIgnoreCase(("TRUE"))){
                winch_HOME.setVisibility(View.VISIBLE);
            }
        }


    }

    private void initviews() {
        cmc_HOME = findViewById(R.id.cmc_linear_layout);
        sp_HOME = findViewById(R.id.spare_parts_linear_layout);
        winch_HOME = findViewById(R.id.winch_linear_layout);
    }


}
