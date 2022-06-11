package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Report;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SendReport extends AppCompatActivity {

    Button sendButn;
    TextInputEditText reportDescriptionBtn;
    DatabaseReference reportsTable;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = SendReport.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        initToolbar();
        forceRTLIfSupported();
        reportsTable = FirebaseDatabase.getInstance().getReference().child("Reports");
        sendButn = (Button) findViewById(R.id.sendButn);
        reportDescriptionBtn = findViewById(R.id.reportdescriptionbtn);
        sendButn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(TextUtils.isEmpty(reportDescriptionBtn.getText().toString().trim())){
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال وصف للمشكلة!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
                else{
                    sendReport(reportDescriptionBtn.getText().toString().trim());
                }
            }
        });
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_addreportdata);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("إرسال تقرير/مشكلة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SendReport.this, R.style.Theme_ResQme);
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


    private void sendReport(String reportDesc) {

        new AlertDialog.Builder(SendReport.this, R.style.AlertDialogCustom)
                .setTitle("إرسال تقرير")
                .setMessage("هل أنت متأكد من إرسالك لهذا التقرير؟")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("جاري إرسال البيانات...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String reportID = database.getReference("Reports").push().getKey(); // create new id
                        Report report = new Report(reportDesc, reportID, mAuth.getCurrentUser().getUid(),"Pending", mAuth.getCurrentUser().getEmail());
                        reportsTable.child(reportID).setValue(report);//Entering report in database
                        progressDialog.dismiss();
                        Snackbar.make(SendReport.this.findViewById(android.R.id.content),"تم إرسال التقرير وهو في مرحلة المراجعة، سيتم التواصل معك عن طريق البريد الإلكتروني",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.blue_back))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        LogData.saveLog("APP_CLICK","","","CLICK ON SEND REPORT BUTTON", "SEND_REPORT");
                        reportDescriptionBtn.setText("");
                    }
                })
                .setNegativeButton("لا", null)
                .show();


    }
}