package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class CMCDetails extends AppCompatActivity {
    ImageView cmcImageView;
    TextView cmcName, cmcAddress, cmcAvailability, cmcCarType;
    MaterialButton BtnRequestCMCFromDetails;

    //CMC Data
    String cmcID_STR, cmcNameSTR, cmcAvailabilitySTR, cmcOwnerID,
            cmcCarTypeSTR, cmcImageSTR, cmcStatusSTR, cmcAddressSTR;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmcdetails);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = CMCDetails.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        initViews();
        forceRTLIfSupported();
        Intent intent = getIntent();
        cmcID_STR = intent.getStringExtra("CMC_ID");
        cmcNameSTR = intent.getStringExtra("CMC_NAME");
        cmcAvailabilitySTR = intent.getStringExtra("CMC_AVAILABILITY");
        cmcOwnerID = intent.getStringExtra("CMC_OWNER_ID");
        cmcCarTypeSTR = intent.getStringExtra("CMC_CARTYPE");
        cmcImageSTR = intent.getStringExtra("CMC_IMAGE");
        cmcStatusSTR = intent.getStringExtra("CMC_STATUS");
        cmcAddressSTR = intent.getStringExtra("CMC_ADDRESS");


        Glide.with(this).load(cmcImageSTR).into(cmcImageView);
        cmcName.setText(cmcNameSTR);
        cmcCarType.setText(cmcCarTypeSTR);
        cmcAddress.setText(cmcAddressSTR);
        cmcAvailability.setText(cmcAvailabilitySTR);
        if(cmcAvailabilitySTR.equals("Available")){
            cmcAvailability.setText("متاح");
            cmcAvailability.setTextColor(Color.GREEN);
        }else{
            cmcAvailability.setText("غير متاح");
            cmcAvailability.setTextColor(Color.rgb(255, 166, 53));
        }


        BtnRequestCMCFromDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go To CMC Processing Request Page
                Intent goToCMCProcessingRequest = new Intent(CMCDetails.this, ProcessingRequestCMC.class);
                goToCMCProcessingRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goToCMCProcessingRequest.putExtra("CMC_ID",cmcID_STR);
                goToCMCProcessingRequest.putExtra("CMC_NAME",cmcNameSTR);
                goToCMCProcessingRequest.putExtra("CMC_AVAILABILITY",cmcAvailabilitySTR);
                goToCMCProcessingRequest.putExtra("CMC_OWNER_ID",cmcOwnerID);
                goToCMCProcessingRequest.putExtra("CMC_CARTYPE",cmcCarTypeSTR);
                goToCMCProcessingRequest.putExtra("CMC_IMAGE",cmcImageSTR);
                goToCMCProcessingRequest.putExtra("CMC_STATUS",cmcStatusSTR);
                goToCMCProcessingRequest.putExtra("CMC_ADDRESS",cmcAddressSTR);
                startActivity(goToCMCProcessingRequest);
                LogData.saveLog("SERVICE_CLICK",cmcID_STR,"CMC","", "CMC_DETAILS");
            }
        });

    }

    private void initViews() {
        cmcImageView = findViewById(R.id.cmc_image_detailspage);
        cmcName = findViewById(R.id.cmcname_detailspag);
        cmcAddress = findViewById(R.id.cmc_address_detailspag);
        cmcAvailability = findViewById(R.id.cmc_availability_detailspag);
        cmcCarType = findViewById(R.id.cmc_cartype_detailspag);
        BtnRequestCMCFromDetails = findViewById(R.id.request_cmc_btn_in_details_page);
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_cmc_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تفاصيل مركز الصيانة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CMCDetails.this, R.style.Theme_ResQme);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}