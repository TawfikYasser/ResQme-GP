package com.example.resqme.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Context context;
    String serviceCost = "", PaymentStatusArg= "";
    ProgressDialog progressDialogPayment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_request_winch);
        initToolbar();
        forceRTLIfSupported();
        context = this;
        Intent intent = getIntent();
        serviceCost = intent.getStringExtra("PAYMENT_COST");
        progressDialogPayment = new ProgressDialog(this);
        sendDescriptionOfWinchRequest = findViewById(R.id.send_winch_request_btn);
        etWinchRequestDescription = findViewById(R.id.send_winch_request_description_et);
        sendDescriptionOfWinchRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etWinchRequestDescription.getText().toString().trim())){
                    new AlertDialog.Builder(ProcessingRequestWinch.this)
                            .setTitle("طلب ونش")
                            .setMessage("سيتم تحويلك الآن الى صفحة الدفع...")
                            .setPositiveButton("متابعة", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Send desc to payment page
                                    progressDialogPayment.setMessage("تفعيل خدمات الدفع...");
                                    progressDialogPayment.show();
                                    Intent paymentIntent = new Intent(context, CustomerWinchPayment.class);
                                    paymentIntent.putExtra("SERVICE_COST", serviceCost);
                                    startActivityForResult(paymentIntent, 30);
                                }
                            })
                            .setNegativeButton("إلغاء", null)
                            .show();
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال وصف للطلب قبل الضغط على إرسال.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 30){
            // If payment done, we can send the request
            if(data!=null){
                if(!TextUtils.isEmpty(data.getStringExtra("PAYMENT_STATUS"))){
                    PaymentStatusArg = data.getStringExtra("PAYMENT_STATUS");
                    if(!TextUtils.isEmpty(PaymentStatusArg) && PaymentStatusArg.equals("SUCCESS_P_RESQME")){
                        progressDialogPayment.dismiss();
                        // Here, description added, payment done
                        // go back and send the request
                        Intent getDescriptionBack = new Intent();
                        getDescriptionBack.putExtra("DESC_WINCH_VALUE", etWinchRequestDescription.getText().toString().trim());
                        setResult(25, getDescriptionBack);
                        finish();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialogPayment.dismiss();
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