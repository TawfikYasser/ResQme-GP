package com.example.resqme.serviceProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.customer.CustomerHome;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ServiceProviderAddService extends AppCompatActivity implements View.OnClickListener {
    ImageView sp,cmc,winch;
    Context context;
    Locale locale;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_add_service);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = ServiceProviderAddService.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
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
                LogData.saveLog("APP_CLICK","","","CLICK ON ADD SPARE PARTS BUTTON", "ADD_SERVICE");
                finish();
                break;
            case R.id.CMC_btn:
                Intent toAddcmcintent = new Intent(this,AddCmc.class);
                startActivity(toAddcmcintent);
                LogData.saveLog("APP_CLICK","","","CLICK ON ADD CMC BUTTON", "ADD_SERVICE");
                finish();
                break;
            case R.id.Towing_Car_btn:
                Intent toAddWinchintent = new Intent(this,AddWinchData.class);
                startActivity(toAddWinchintent);
                LogData.saveLog("APP_CLICK","","","CLICK ON ADD WINCH BUTTON", "ADD_SERVICE");
                finish();
                break;
        }
    }
}