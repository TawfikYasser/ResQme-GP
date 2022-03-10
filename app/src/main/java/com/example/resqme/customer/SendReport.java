package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Report;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendReport extends AppCompatActivity {

    Button sendButn;
    TextInputEditText reportDescriptionBtn;
    DatabaseReference reportsTable;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        progressDialog = new ProgressDialog(this);

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
                    progressDialog.setMessage("جاري إرسال البيانات...");
                    progressDialog.show();
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String reportID = database.getReference("Reports").push().getKey();
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID","C_DEFAULT");
        String c_email = userData.getString("C_EMAIL", "C_DEFAULT");
        Report report = new Report(reportDesc, reportID, c_userid,"Pending", c_email);
        reportsTable.child(reportID).setValue(report);
        progressDialog.dismiss();
        Toast.makeText(this, "تم إرسال التقرير وهو في مرحلة المراجعة، سيتم التواصل معك عن طريق البريد الإلكتروني", Toast.LENGTH_LONG).show();
        finish();
    }
}