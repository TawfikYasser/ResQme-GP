package com.example.resqme.serviceProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.google.android.material.button.MaterialButton;

public class ServiceProviderAddService extends AppCompatActivity implements View.OnClickListener {
    Button sp,cmc,winch;
    Context context;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_add_service);
        context = getApplicationContext();
        initViews();
    }


    void initViews(){
        sp = findViewById(R.id.Spare_Parts_btn);
        sp.setOnClickListener(this);
        cmc = findViewById(R.id.CMC_btn);
        cmc.setOnClickListener(this);
        winch = findViewById(R.id.Towing_Car_btn);
        winch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Spare_Parts_btn:
                Intent toAddsparepartintent = new Intent(this,AddSpareParts.class);
                startActivity(toAddsparepartintent);
                break;
            case R.id.CMC_btn:
                Intent toAddcmcintent = new Intent(this,AddCmc.class);
                startActivity(toAddcmcintent);
                break;
            case R.id.Towing_Car_btn:
                Intent toAddWinchintent = new Intent(this,AddWinchData.class);
                startActivity(toAddWinchintent);
                break;
        }
    }
}