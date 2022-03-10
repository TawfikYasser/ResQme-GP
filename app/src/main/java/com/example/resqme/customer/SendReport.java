package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendReport extends AppCompatActivity {
    Button sendButn;
    EditText reportDescriptionBtn;
    DatabaseReference reportsTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);
        reportsTable = FirebaseDatabase.getInstance().getReference().child("Reports");
        sendButn = (Button) findViewById(R.id.sendButn);
        reportDescriptionBtn = findViewById(R.id.reportdescriptionbtn);
        sendButn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String reportDesc = reportDescriptionBtn.getText().toString();
                if(reportDesc.equals("")){
                    Toast.makeText(SendReport.this,
                            "من فضلك ادخل التقرير", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    sendReport(reportDesc);
                }
            }
        });
    }

    private void sendReport(String reportDesc) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String reportID = database.getReference("Reports").push().getKey();
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID","C_DEFAULT");

        Report report = new Report(reportDesc,reportID,c_userid,"Pending");
        reportsTable.child(reportID).setValue(report);
        finish();

    }
}