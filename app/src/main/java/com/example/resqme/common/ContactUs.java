package com.example.resqme.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactUs extends AppCompatActivity {
    CircleImageView contactUsWhatsApp, contactUsFacebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initToolbar();
        forceRTLIfSupported();
        contactUsWhatsApp = findViewById(R.id.contactus_whatsapp);
        contactUsFacebook = findViewById(R.id.contactus_facebook);

        contactUsWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=201129348206"; // Test Link
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                LogData.saveLog("APP_CLICK","","","CLICK ON WHATSAPP ICON", "CONTACT_US");
            }
        });

        contactUsFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:resqmeservices@gmail.com")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "ايميل من عميل في تطبيق ResQme");
                intent.putExtra(Intent.EXTRA_SUBJECT, "نص الايميل...");
                startActivity(intent);
                LogData.saveLog("APP_CLICK","","","CLICK ON EMAIL ICON", "CONTACT_US");
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_contactus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تواصل معنا");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ContactUs.this, R.style.Theme_ResQme);
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