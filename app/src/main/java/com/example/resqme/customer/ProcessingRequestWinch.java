package com.example.resqme.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resqme.R;
import com.example.resqme.common.InternetConnection;
import com.example.resqme.common.LogData;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ProcessingRequestWinch extends AppCompatActivity{
    Button sendDescriptionOfWinchRequest;
    TextInputEditText etWinchRequestDescription;
    Context context;
    String serviceCost = "", PaymentStatusArg= "";
    ProgressDialog progressDialogPayment;

    // Payment

    String PUBLISH_KEY="pk_test_51Ks41UDbx4QPlP6U8rsvuD3nnuNSZJ1ZZmuxJctI7LJx89zYZ1BA8sU0HNFarzWRb4H5WYtsQJVCTF12cjmbIu990033LD1FLc";
    String SECRET_KEY="sk_test_51Ks41UDbx4QPlP6Uq0UhoJLpAzaBhfTYgy81lRgNm5SsatgN1YwTzCOKbDV4ifYSEglwFnh5pvSyyWKhGxldSPK300kk542HML";
    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;
    InternetConnection ic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_request_winch);
        initToolbar();
        forceRTLIfSupported();
        context = this;
        ic = new InternetConnection(context);
        Intent intent = getIntent();
        serviceCost = intent.getStringExtra("PAYMENT_COST");
        progressDialogPayment = new ProgressDialog(this);

        PaymentConfiguration.init(ProcessingRequestWinch.this, PUBLISH_KEY);
        paymentSheet = new PaymentSheet(ProcessingRequestWinch.this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        sendDescriptionOfWinchRequest = findViewById(R.id.send_winch_request_btn);
        etWinchRequestDescription = findViewById(R.id.send_winch_request_description_et);
        sendDescriptionOfWinchRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etWinchRequestDescription.getText().toString().trim())){
                    if(ic.checkInternetConnection()){
                        new AlertDialog.Builder(ProcessingRequestWinch.this)
                                .setTitle("طلب ونش")
                                .setMessage("هل تريد المتابعة الى عملية الدفع؟")
                                .setPositiveButton("متابعة", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Send desc to payment page
                                        progressDialogPayment.setMessage("تفعيل خدمات الدفع...");
                                        progressDialogPayment.show();
                                        progressDialogPayment.setCancelable(false);
                                        LogData.saveLog("USER CLICKED ON PROCESSING WINCH REQUEST BUTTON","TRUE","","FALSE");
                                        goToPay();
                                    }
                                })
                                .setNegativeButton("إلغاء", null)
                                .show();
                    }else{
                        Snackbar.make(ProcessingRequestWinch.this.findViewById(android.R.id.content),"لا يوجد إتصال بالإنترنت.",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.red_color))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال وصف للطلب قبل الضغط على إرسال.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Snackbar.make(ProcessingRequestWinch.this.findViewById(android.R.id.content),"تمت عملية الدفع بنجاح",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.blue_back))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            Intent getDescriptionBack = new Intent();
            getDescriptionBack.putExtra("DESC_WINCH_VALUE", etWinchRequestDescription.getText().toString().trim());
            setResult(25, getDescriptionBack);
            finish();
        }else{
            Snackbar.make(ProcessingRequestWinch.this.findViewById(android.R.id.content),"لم تتم عملية الدفع",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
    }

    private void goToPay() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            Toast.makeText(context, "الحصول على معلومات التوثيق...", Toast.LENGTH_SHORT).show();
                            getEphericalKey(customerID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);

                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProcessingRequestWinch.this);
        requestQueue.add(stringRequest);


    }

    private void getEphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(context, "الحصول على معلومات الإتصال...", Toast.LENGTH_SHORT).show();
                            getClientSecret(customerID, EphericalKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);
                header.put("Stripe-Version", "2020-08-27");

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProcessingRequestWinch.this);
        requestQueue.add(stringRequest);


    }

    private void getClientSecret(String customerID, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            Toast.makeText(context, "نجح الإتصال...", Toast.LENGTH_SHORT).show();
                            progressDialogPayment.dismiss();
                            paymentFlowStarting();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    System.out.println(body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", serviceCost+"00");
                params.put("currency", "EGP");
                params.put("automatic_payment_methods[enabled]", "true");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProcessingRequestWinch.this);
        requestQueue.add(stringRequest);

    }

    private void paymentFlowStarting() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration("ResQme App",
                        new PaymentSheet.CustomerConfiguration(
                                customerID, EphericalKey
                        ))
        );

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