package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.resqme.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProcessingRequestWinch extends AppCompatActivity {
    Button sendDescriptionOfWinchRequest;
    TextInputEditText etWinchRequestDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_request_winch);
        initToolbar();
        forceRTLIfSupported();
        sendDescriptionOfWinchRequest = findViewById(R.id.send_winch_request_btn);
        etWinchRequestDescription = findViewById(R.id.send_winch_request_description_et);
        sendDescriptionOfWinchRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etWinchRequestDescription.getText().toString().trim())){
                    Intent getDescriptionBack = new Intent();
                    getDescriptionBack.putExtra("DESC_WINCH_VALUE", etWinchRequestDescription.getText().toString().trim());
                    setResult(25, getDescriptionBack);
                    finish();
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال وصف للطلب قبل الضغط على إرسال.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_processing_request_winch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("إتمام الطلب");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ProcessingRequestWinch.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}