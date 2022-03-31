package com.example.resqme.common;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.customer.AddCarData;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class RatingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_screen);
        forceRTLIfSupported();
        initToolbar();
        Locale.setDefault(new Locale("ar"));

    }
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_rating);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("رأيك في الخدمة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(RatingScreen.this, R.style.Theme_ResQme);
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