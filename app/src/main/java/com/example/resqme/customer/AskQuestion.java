package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.LogData;
import com.example.resqme.model.Question;
import com.example.resqme.model.Report;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class AskQuestion extends AppCompatActivity {
    Button sendQuestion;
    TextInputEditText questionET;
    DatabaseReference questionDB;
    ProgressDialog progressDialog;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = AskQuestion.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        forceRTLIfSupported();
        initToolbar();
        progressDialog = new ProgressDialog(this);
        questionDB = FirebaseDatabase.getInstance().getReference().child("Questions");
        sendQuestion = (Button) findViewById(R.id.send_question_btn);
        questionET = findViewById(R.id.ask_question_et);
        sendQuestion.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(TextUtils.isEmpty(questionET.getText().toString().trim())){
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال نص السؤال قبل الضغط على إرسال!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
                else{
                    sendQuestion(questionET.getText().toString().trim());
                }
            }
        });

    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_ask_question);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اسال سؤال");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AskQuestion.this, R.style.Theme_ResQme);
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


    private void sendQuestion(String question) {

        new AlertDialog.Builder(AskQuestion.this, R.style.AlertDialogCustom)
                .setTitle("إرسال سؤال")
                .setMessage("هل أنت متأكد من إرسالك لهذا السؤال؟")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("جاري إرسال البيانات...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String questionID = database.getReference("Questions").push().getKey();// create new id
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                        String c_userid = userData.getString("C_USERID","C_DEFAULT");
                        Question questionObj = new Question(questionID, c_userid, question);
                        questionDB.child(questionID).setValue(questionObj);//Entering question in database
                        progressDialog.dismiss();
                        questionET.setText("");
                        LogData.saveLog("APP_CLICK","","","CLICK ON SEND QUESTION BUTTON", "ASK_QUESTION");
                        Snackbar.make(AskQuestion.this.findViewById(android.R.id.content),"تم إرسال السؤال وهو الآن في صفحة الاسئلة.",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.blue_back))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                })
                .setNegativeButton("لا", null)
                .show();
    }
}